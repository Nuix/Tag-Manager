package com.nuix.tagmanager.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.nuix.tagmanager.Tag;

public class TagTreeModel implements TreeModel {

	private List<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	private TagTreeRoot root = new TagTreeRoot();
	
	private Pattern nameFilter = null;
	
	public TagTreeModel(){
		
	}
	
	private List<Tag> filterTags(List<Tag> toFilter){
		if(nameFilter != null){
			List<Tag> filtered = new ArrayList<Tag>();
			for(Tag t : toFilter){
				boolean matchFound = false;
				
				if(nameFilter.matcher(t.getName()).find()){
					matchFound = true;
				}
				
				if(!matchFound){
					List<Tag> ancestors = t.getAncestors();
					for(Tag ancestor : ancestors){
						if(nameFilter.matcher(ancestor.getName()).find()){
							matchFound = true;
							break;
						}
					}
				}
				
				if(!matchFound){
					List<Tag> descendants = t.getAllDescendantTags();
					for(Tag descendant : descendants){
						if(nameFilter.matcher(descendant.getName()).find()){
							matchFound = true;
							break;
						}
					}
				}
				
				if(matchFound){
					filtered.add(t);
				}
			}
			return filtered;	
		} else {
			return toFilter;
		}
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent instanceof TagTreeRoot){
			List<Tag> rootTags = ((TagTreeRoot)parent).getRootTags();
			rootTags = filterTags(rootTags);
			return rootTags.get(index);
		} else {
			List<Tag> children = ((Tag)parent).getChildren();
			children = filterTags(children);
			return children.get(index);
		}
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent instanceof TagTreeRoot){
			List<Tag> rootTags = ((TagTreeRoot)parent).getRootTags();
			rootTags = filterTags(rootTags);
			return rootTags.size();
		} else {
			List<Tag> children = ((Tag)parent).getChildren();
			children = filterTags(children);
			return children.size();
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent instanceof TagTreeRoot){
			List<Tag> rootTags = ((TagTreeRoot)parent).getRootTags();
			rootTags = filterTags(rootTags);
			return rootTags.indexOf(child);
		} else {
			List<Tag> children = ((Tag)parent).getChildren();
			children = filterTags(children);
			return children.indexOf(child);
		}
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		if(node instanceof TagTreeRoot){
			List<Tag> rootTags = ((TagTreeRoot)node).getRootTags();
			rootTags = filterTags(rootTags);
			return rootTags.size() < 1;
		} else {
			List<Tag> children = ((Tag)node).getChildren();
			children = filterTags(children);
			return children.size() < 1;
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}
	
	public void setFilter(String filterString){
		if(filterString.trim().isEmpty()){
			clearFilter();
		} else {
			try	{
				nameFilter = Pattern.compile(filterString,Pattern.CASE_INSENSITIVE);	
			} catch(Exception exc){
				nameFilter = null;
			}
			notifyDataChanged();	
		}
	}
	
	public void clearFilter(){
		nameFilter = null;
		notifyDataChanged();
	}
	
	private void notifyDataChanged(){
		if(treeModelListeners.size() > 0){
			for(TreeModelListener l : treeModelListeners){
				l.treeStructureChanged(new TreeModelEvent(root,new Object[]{root}));
			}
		}
	}

	public void reloadFromCase() throws Exception{
		Tag.buildTagStructure();
		notifyDataChanged();
	}
}
