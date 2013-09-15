package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

public interface ITreasureChestGroupRepository {
	
	public ITreasureChestGroup getGroup(String name);
	
	public void setGroup(String name, ITreasureChestGroup value);
	
	public boolean hasGroup(String name);
	
	public boolean removeGroup(String name);

	public Set<String> getGroupNames();
	
}
