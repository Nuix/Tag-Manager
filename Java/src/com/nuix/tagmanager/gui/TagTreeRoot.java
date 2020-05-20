package com.nuix.tagmanager.gui;

import java.util.List;

import com.nuix.tagmanager.Tag;

public class TagTreeRoot {
	public List<Tag> getRootTags(){
		return Tag.getRootTags();
	}
	
	@Override
	public String toString() {
		return "Case Tags";
	}
}
