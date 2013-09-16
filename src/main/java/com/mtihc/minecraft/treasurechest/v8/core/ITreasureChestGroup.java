package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface that represents a group of treasures.
 *  
 * @author Mitch
 *
 */
public interface ITreasureChestGroup extends ConfigurationSerializable {

	/**
	 * Add a treasure to this group
	 * @param value the treasure
	 * @throws Exception thrown when the treasure could not be added to this group
	 */
	public void addChest(ITreasureChest value) throws Exception;

	/**
	 * Remove a treasure from this group
	 * @param value the treasure
	 * @return true if the treasure was removed from the group
	 * @throws Exception thrown when the treasure could not be removed from this group
	 */
	public boolean removeChest(ITreasureChest value) throws Exception;
	
	/**
	 * Returns the locations of the treasures that are in this group.
	 * @return the locations of the treasures that are in this group.
	 */
	public Set<Location> getLocations();
}
