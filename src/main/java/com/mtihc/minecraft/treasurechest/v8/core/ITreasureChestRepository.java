package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;

public interface ITreasureChestRepository {

	public ITreasureChest load(Location location);
	
	public void save(Location location, ITreasureChest value);
	
	public boolean has(Location location);
	
	public void delete(Location location);
	
	public Set<Location> getLocations(String worldName);
}
