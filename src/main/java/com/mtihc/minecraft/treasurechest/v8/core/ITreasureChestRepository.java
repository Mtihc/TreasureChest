package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

import org.bukkit.Location;

/**
 * Interface that provides methods to save/load treasures.
 * 
 * @author Mitch
 *
 */
public interface ITreasureChestRepository {

	/**
	 * Load the treasure at the specified location.
	 * @param location the location of the treasure
	 * @return the treasure
	 */
    ITreasureChest getTreasure(Location location);
	
	/**
	 * Save the specified treasure
	 * @param value the treasure
	 */
    void setTreasure(ITreasureChest value);
	
	/**
	 * Returns whether there is a treasure at the specified location
	 * @param location the location to check
	 * @return true if there's a treasure, false if there's not
	 */
    boolean hasTreasure(Location location);
	
	/**
	 * Delete the treasure at the specified location
	 * @param location the location of the treasure
	 * @return true if a treasure was deleted, false otherwise
	 */
    boolean removeTreasure(Location location);
	
	/**
	 * Returns the locations of all treasures in the specified world
	 * @param worldName the world name
	 * @return the locations of all treasures in the specified world
	 */
    Set<Location> getTreasureLocations(String worldName);
}
