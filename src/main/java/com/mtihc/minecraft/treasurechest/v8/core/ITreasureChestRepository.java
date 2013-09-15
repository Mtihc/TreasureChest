package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;

public interface ITreasureChestRepository {

	public ITreasureChest getTreasure(Location location);
	
	public void setTreasure(ITreasureChest value);
	
	public boolean hasTreasure(Location location);
	
	public boolean removeTreasure(Location location);
	
	public Set<Location> getTreasureLocations(String worldName);
}
