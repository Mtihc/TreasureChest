package com.mtihc.minecraft.treasurechest.v8.core;

public interface ITreasureChestGroupRepository {
	
	public ITreasureChestGroup load(String name);
	
	public void save(String name, ITreasureChestGroup value);
	
	public boolean exists(String name);
	
	public void delete(String name);
	
}
