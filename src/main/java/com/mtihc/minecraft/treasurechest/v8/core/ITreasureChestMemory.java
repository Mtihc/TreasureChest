package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

/**
 * Interface that provides methods to get information about who found which treasure, etc.
 * @author Mitch
 *
 */
public interface ITreasureChestMemory {
	
	/**
	 * Returns the locations of all treasures that the specified player has found in the specified world.
	 * @param player the player
	 * @param world the world
	 * @return the locations of all found treasures
	 */
	Collection<Location> getAllPlayerFound(OfflinePlayer player, World world);
	
	/**
	 * Returns the timestamp of the moment that the specified player found the treasure at the specified location.
	 * Returns zero, when the specified player has not yet found the treasure at the specified location.
	 * 
	 * <p>This method is checked whenever a player tries to open a treasure that has a forget-time.</p>
	 * 
	 * @param player the player
	 * @param location the location of the treasure
	 * @return the timestamp of the moment that the treasure was found.
	 */
	long whenHasPlayerFound(OfflinePlayer player, Location location);
	
	/**
	 * Returns whether the specified player has found the treasure at the specified location.
	 * 
	 * <p>This method is checked whenever a player tries to open a treasure.</p>
	 * 
	 * @param player the player
	 * @param location the location of the treasure
	 * @return true if the player has found the treasure, false otherwise
	 */
	boolean hasPlayerFound(OfflinePlayer player, Location location);
	
	/**
	 * Remember that the specified player just found the treasure at the specified location.
	 * 
	 * <p>This method is called whenever a player opens a treasure.</p>
	 * 
	 * @param player the player
	 * @param location the location of the treasure
	 */
	void rememberPlayerFound(OfflinePlayer player, Location location);
	
	/**
	 * Forget that the specified player found the treasure, if he/she found it before.
	 * 
	 * @param player the player
	 * @param location the location of the treasure
	 */
	void forgetPlayerFound(OfflinePlayer player, Location location);
	
	/**
	 * Forget that the specified player found any treasure in the specified world.
	 * 
	 * @param player the player
	 * @param world the world
	 */
	void forgetPlayerFoundAll(OfflinePlayer player, World world);
	
	/**
	 * Forget that anybody found the treasure at the specified location.
	 * 
	 * @param location the location of the treasure
	 */
	void forgetChest(Location location);
}
