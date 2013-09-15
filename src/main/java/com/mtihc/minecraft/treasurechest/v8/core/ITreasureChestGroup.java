package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ITreasureChestGroup extends ConfigurationSerializable {

	public void addChest(ITreasureChest chest) throws Exception;

	public boolean removeChest(ITreasureChest chest) throws Exception;
	
	public Set<Location> getLocations();
	
	//public String getError();
}
