package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ITreasureChestGroup extends ConfigurationSerializable {

	public boolean addChest(ITreasureChest chest);

	public boolean removeChest(ITreasureChest chest);
	
	public Set<Location> getLocations();
	
	public String getError();
}
