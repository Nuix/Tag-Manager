package com.nuix.tagmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.nuix.tagmanager.gui.ProgressDialog;
import com.nuix.tagmanager.gui.ProgressDialogBlockInterface;

import nuix.BulkAnnotater;
import nuix.Case;
import nuix.Item;
import nuix.ItemEventCallback;
import nuix.ItemEventInfo;

public class Tag {
	private static Logger logger = Logger.getLogger(Tag.class);
	
	private static Map<String,Tag> lookup = new HashMap<String,Tag>();
	private static List<Tag> allTags = new ArrayList<Tag>();
	
	private Tag parent = null;;
	private List<Tag> children = new ArrayList<Tag>();
	private String name = null;
	private String tailName = null;
	
	protected Tag(){}
	
	public static Tag construct(String tagName){
		//NuixConnection.logMessage("Constructing Tag Object: "+tagName);
		
		if(lookup.containsKey(tagName)){
			return lookup.get(tagName);
		} else {
			Tag result = new Tag();
			result.name = tagName;
			List<String> pieces = splitTagName(tagName);
			result.tailName = pieces.get(pieces.size()-1);
			if(pieces.size() > 1){
				pieces.remove(pieces.size()-1);
				String parentTagName = String.join("|", pieces);
				if(lookup.containsKey(parentTagName)){
					lookup.get(parentTagName).addChild(result);
				}
			}
			lookup.put(tagName, result);
			allTags.add(result);
			return result;
		}
	}
	
	public static List<String> splitTagName(String tagName){
		List<String> pieces = new ArrayList<String>();
		for(String piece : tagName.split("\\|")){pieces.add(piece);}
		return pieces;
	}
	
	public static List<Tag> buildTagStructure(Case nuixCase) throws Exception{
		lookup.clear();
		allTags.clear();
		List<String> tagNames = new ArrayList<String>();
		if(nuixCase != null){
			tagNames.addAll(nuixCase.getAllTags());
			Collections.sort(tagNames);
			for(String tagName : tagNames){
				construct(tagName);
			}	
		}
		
//		NuixConnection.logMessage("buildTagStructure completed");
//		NuixConnection.logMessage("Root Tags: "+getRootTags().size());
//		NuixConnection.logMessage("All Tags: "+getAllTags().size());
		
		return getRootTags();
	}
	
	public static List<Tag> buildTagStructure() throws Exception{
		return buildTagStructure(NuixConnection.getCurrentCase());
	}
	
	public static List<Tag> getRootTags(){
		return allTags.stream().filter(t -> t.isRootTag()).collect(Collectors.toList());
	}
	
	public static Tag findTagByName(String tagName){
		if(lookup.containsKey(tagName)){
			return lookup.get(tagName);
		} else {
			return null;
		}
	}
	
	public static boolean tagsHaveSameParent(Collection<Tag> tags){
		String expectedParentTagName = null;
		for(Tag tag : tags){
			String parentName = null;
			if(tag.isRootTag()){
				parentName = "";
			} else {
				parentName = tag.getParent().getName();
			}
			
			if(expectedParentTagName == null){
				expectedParentTagName = parentName;
			} else {
				if(!parentName.equals(expectedParentTagName)){
					return false;
				}
			}
		}
		return true;
	}
	
	public static String escapeTagForSearch(String tagName){
		String result = tagName;
		result = result.replace("\\", "\\\\");
		result = result.replace("?", "\\?");
		result = result.replace("*", "\\*");
		result = result.replace("\"", "\\\"");
		result = result.replace("'", "\\'");
		result = result.replace("‘", "\\‘");
		result = result.replace("’", "\\’");
		result = result.replace("“", "\\“");
		result = result.replace("�?", "\\�?");
		return result;
	}
	
	public static void deleteTags(Case nuixCase, List<Tag> tagsToDelete) throws Exception{
		ProgressDialog.forBlock(new ProgressDialogBlockInterface() {
			@Override
			public void DoWork(ProgressDialog pd) {
				pd.setTitle("Delete Tags");
				pd.setMainStatusAndLogIt("Determining tag and descendant tags...");
				List<Tag> toDelete = findTagsAndDescendants(tagsToDelete);
				pd.logMessage("Total Tags: "+toDelete.size());
				
				pd.setMainStatusAndLogIt("Ordering tags for removal and deletion...");
				toDelete.sort((a,b) -> a.getName().compareTo(b.getName()));
				Collections.reverse(toDelete);
				
				BulkAnnotater annotater = NuixConnection.getUtilities().getBulkAnnotater();
				
				pd.setMainStatusAndLogIt("Beginning removal...");
				pd.setMainProgress(0,toDelete.size());
				for (int i = 0; i < toDelete.size(); i++) {
					if(pd.abortWasRequested()){ break; }
					pd.setMainProgress(i+1);
					pd.setMainStatus("Removing Tag "+(i+1)+"/"+toDelete.size());
					Tag t = toDelete.get(i);
					try {
						pd.setSubStatus("Locating items with tag...");
						Set<Item> itemsWithTag = t.getTaggedItems(nuixCase);
						
						pd.setSubStatus("Removing tag from items...");
						pd.logMessage("Removing '"+t.getName()+"' from "+itemsWithTag.size()+" items");
						annotater.removeTag(t.getName(), itemsWithTag,new ItemEventCallback() {
							@Override
							public void itemProcessed(ItemEventInfo info) {
								Long stageCount = info.getStageCount();
								pd.setSubProgress(stageCount.intValue(), itemsWithTag.size());
							}
						});
						
						pd.setSubStatus("Deleting tag from case...");
						nuixCase.deleteTag(t.getName());
						
						pd.setSubStatus("");
					} catch (Exception e) {
						pd.logMessage("Error while removing/deleting tag '"+t.getName()+"': "+e.getMessage());
						logger.error("Error while removing/deleting tag '"+t.getName()+"': ",e);
					}
				}
				
				if(pd.abortWasRequested()){ pd.logMessage("User Aborted"); }
				else { pd.setCompleted(); }
			}
		});
	}
	
	public Set<Item> getTaggedItems(Case nuixCase) throws Exception{
		return nuixCase.searchUnsorted("tag:\""+escapeTagForSearch(getName())+"\"");
	}
	
	public long getTaggedItemCount(Case nuixCase) throws Exception{
		return nuixCase.count("tag:\""+escapeTagForSearch(getName())+"\"");
	}
	
	public void changeParentTag(Case nuixCase, String newParentTag, TagCleanupOption cleanup) throws Exception{
		ProgressDialog.forBlock(new ProgressDialogBlockInterface() {
			@Override
			public void DoWork(ProgressDialog pd) {
				pd.setTitle("Copy/Move Tag");
				pd.setAbortButtonVisible(false);
				
				pd.logMessage("Old Tag Handling: "+cleanup);
				
				String newParentTagName = newParentTag.trim();
				pd.logMessage("New Parent Tag: "+newParentTag);
				
				List<Tag> toUpdate = getTagAndDescendants();
				Collections.reverse(toUpdate);
				pd.logMessage("Tags to Update: "+toUpdate.size());
				
				BulkAnnotater annotater = NuixConnection.getUtilities().getBulkAnnotater();
				
				for (int i = 0; i < toUpdate.size(); i++) {
					pd.setMainProgress(i+1,toUpdate.size());
					Tag t = toUpdate.get(i);
					String newTagName = null;
					if(!isRootTag()){
						String patternString = "^"+Pattern.quote(getParent().getName())+"\\|";
						Pattern parentPattern = Pattern.compile(patternString);
						if(!newParentTagName.isEmpty()){
							newTagName = parentPattern.matcher(t.getName()).replaceAll(newParentTagName+"|");
						} else {
							newTagName = parentPattern.matcher(t.getName()).replaceAll(newParentTagName);
						}
					} else {
						newTagName = newParentTagName+"|"+t.getName();
					}
					
					pd.logMessage("==> Old Tag: "+t.getName());
					pd.logMessage("Is Root: "+t.isRootTag());
					pd.logMessage("<== New Tag: "+newTagName);
					
					try {
						pd.setMainStatus(String.format("Processing (%s/%s): %s", i+1, toUpdate.size(), t.getName()));
						
						pd.setSubStatus("Locating tagged items...");
						Set<Item> items = t.getTaggedItems(nuixCase);
						
						pd.setSubStatus("Creating Tag: "+newTagName);
						nuixCase.createTag(newTagName);
						
						pd.setSubStatus("Applying '"+newTagName+"' to "+items.size()+" items");
						pd.setSubProgress(0,items.size());
						annotater.addTag(newTagName, items, new ItemEventCallback() {
							@Override
							public void itemProcessed(ItemEventInfo info) {
								Long stageCount = info.getStageCount();
								pd.setSubProgress(stageCount.intValue());
							}
						});
						
						switch (cleanup) {
							case remove:
								pd.setSubStatusAndLogIt("Removing '"+t.getName()+"' from "+items.size()+" items");
								annotater.removeTag(t.getName(), items, new ItemEventCallback() {
									@Override
									public void itemProcessed(ItemEventInfo info) {
										Long stageCount = info.getStageCount();
										pd.setSubProgress(stageCount.intValue());
									}
								});
								break;
							case removeAndDelete:
								pd.setSubStatusAndLogIt("Removing '"+t.getName()+"' from "+items.size()+" items");
								annotater.removeTag(t.getName(), items, new ItemEventCallback() {
									@Override
									public void itemProcessed(ItemEventInfo info) {
										Long stageCount = info.getStageCount();
										pd.setSubProgress(stageCount.intValue());
									}
								});
								pd.setSubStatus("Deleteing tag: "+t.getName());
								nuixCase.deleteTag(t.getName());
								break;
							case none:
							default:
								break;
						}
					} catch (Exception e) {
						pd.logMessage("Error changing tag '"+t.getName()+"': "+e.getMessage());
						logger.error("Error changing tag '"+t.getName()+"': ",e);
					}
					
					pd.setSubStatus("");
					pd.setSubProgress(0);
				}
				
				pd.setCompleted();
			}
		});
	}
	
	public void copyTagStructure(Case nuixCase, String newParentTag) throws Exception{
		changeParentTag(nuixCase, newParentTag, TagCleanupOption.none);
	}
	
	public List<Tag> getTagsWithChildren(){
		return allTags.stream().filter(t -> t.hasChildren()).collect(Collectors.toList());
	}
	
	public boolean isRootTag(){
		return parent == null;
	}
	
	public boolean hasParent(){
		return parent != null;
	}
	
	public boolean hasChildren(){
		return children.size() > 0;
	}
	
	public List<Tag> getAllDescendantTags(){
		List<Tag> result = new ArrayList<Tag>();
		result.addAll(children);
		for(Tag child : children){
			result.addAll(child.getAllDescendantTags());
		}
		return result;
	}
	
	public List<Tag> getTagAndDescendants(){
		List<Tag> result = getAllDescendantTags();
		result.add(this);
		result.sort((a,b) -> a.getName().compareTo(b.getName()));
		return result;
	}
	
	public static List<Tag> findTagsAndDescendants(Collection<Tag> tags){
		List<Tag> result = new ArrayList<Tag>();
		Set<Tag> deduped = new HashSet<Tag>();
		for(Tag t : tags){
			deduped.addAll(t.getTagAndDescendants());
		}
		result.addAll(deduped);
		result.sort((a,b) -> a.getName().compareTo(b.getName()));
		return result;
	}
	
	public void addChild(Tag childTag){
		childTag.parent = this;
		children.add(childTag);
	}

	public Tag getParent() {
		return parent;
	}

	public List<Tag> getChildren() {
		return children;
	}
	
	public List<Tag> getAncestors(){
		List<Tag> result = new ArrayList<Tag>();
		Tag currentTag = this;
		while(currentTag.hasParent()){
			Tag parent = currentTag.getParent();
			result.add(parent);
			currentTag = parent;
		}
		Collections.reverse(result);
		return result;
	}
	
	public int getChildCount(){
		return children.size();
	}

	public String getName() {
		return name;
	}

	public static List<Tag> getAllTags() {
		return allTags;
	}

	@Override
	public String toString() {
		return tailName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tailName == null) ? 0 : tailName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tailName == null) {
			if (other.tailName != null)
				return false;
		} else if (!tailName.equals(other.tailName))
			return false;
		return true;
	}
}
