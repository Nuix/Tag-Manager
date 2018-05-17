package com.nuix.tagmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import nuix.BulkAnnotater;
import nuix.Item;

public class DeferredTagger {
	private Map<String,List<Item>> batches = new HashMap<String,List<Item>>();
	private int maxBatchSize = 5000;
	private static Function<String,List<Item>> mappingFunction = (string) -> {
		return new ArrayList<Item>();
	};
	
	public void addTag(String tag, Item item) throws Exception{
		List<Item> batchItems = batches.computeIfAbsent(tag, mappingFunction);
		batchItems.add(item);
		if(batchItems.size() >= maxBatchSize){
			BulkAnnotater annotater = NuixConnection.getUtilities().getBulkAnnotater();
			annotater.addTag(tag,batchItems);
			batchItems.clear();
		}
	}
	
	public void addTag(String tag, Collection<Item> items) throws Exception{
		List<Item> batchItems = batches.computeIfAbsent(tag, mappingFunction);
		batchItems.addAll(items);
		if(batchItems.size() >= maxBatchSize){
			BulkAnnotater annotater = NuixConnection.getUtilities().getBulkAnnotater();
			annotater.addTag(tag,batchItems);
			batchItems.clear();
		}
	}
	
	public void finalize() throws Exception{
		BulkAnnotater annotater = NuixConnection.getUtilities().getBulkAnnotater();
		for(Map.Entry<String, List<Item>> entry : batches.entrySet()){
			List<Item> batchItems = entry.getValue();
			String tag = entry.getKey();
			annotater.addTag(tag,batchItems);
			batchItems.clear();
		}
	}
}
