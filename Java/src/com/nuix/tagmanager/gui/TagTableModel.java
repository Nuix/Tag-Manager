package com.nuix.tagmanager.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.nuix.tagmanager.Tag;

@SuppressWarnings("serial")
public class TagTableModel extends DefaultTableModel {
	private String[] headers = new String[]{
		"Tag Name",
		"Child Tags",
	};
	
	private List<Tag> tags = new ArrayList<Tag>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	@Override
	public int getRowCount() {
		if(tags == null){
			return 0;
		} else {
			return tags.size();	
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Tag t = tags.get(row);
		
		switch (col) {
			case 0: return t.getName();
			case 1: return t.getChildren().size();
		default:
			return "?";
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
		this.fireTableDataChanged();
	}

}
