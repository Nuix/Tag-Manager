package com.nuix.tagmanager;

public enum TagCleanupOption {
	none("Copy"),
	remove("Remove Tag"),
	removeAndDelete("Remove Tag and Delete It");
	
	private final String name;       

    private TagCleanupOption(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }

}
