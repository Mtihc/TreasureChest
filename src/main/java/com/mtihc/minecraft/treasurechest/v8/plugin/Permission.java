package com.mtihc.minecraft.treasurechest.v8.plugin;

public enum Permission {
	MAIN("treasurechest"),
	SET("treasurechest.set"),
	DEL("treasurechest.delete"),
	FORGET("treasurechest.forget"),
	FORGET_OTHERS("treasurechest.forget.others"),
	FORGET_ALL("treasurechest.forget.all"),
	UNLIMITED("treasurechest.unlimited"),
	COUNT("treasurechest.count"),
	COUNT_OTHERS("treasurechest.count.others"),
	RELOAD("treasurechest.reload"),
	ACCESS_TREASURE("treasurechest.access.treasure"),
	ACCESS_UNLIMITED("treasurechest.access.unlimited"), 
	RANDOM("treasurechest.random"), 
	IGNORE_PROTECTION("treasurechest.ignoreprotection"), 
	LIST("treasurechest.list"), 
	LIST_ALL("treasurechest.list.all"), 
	RANK("treasurechest.rank");
	
	private String node;

	Permission(String node) {
		this.node = node;
	}
	
	public String getNode() {
		return node;
	}
	
}