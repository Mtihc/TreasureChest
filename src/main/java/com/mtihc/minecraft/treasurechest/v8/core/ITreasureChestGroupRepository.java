package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Set;

/**
 * Interface that provides methods to save/load treasure groups.
 * 
 * @author Mitch
 *
 */
public interface ITreasureChestGroupRepository {
	
	/**
	 * Load the group with the specified name
	 * @param name the group name
	 * @return the group
	 */
    ITreasureChestGroup getGroup(String name);
	
	/**
	 * Save the specified group
	 * @param name the group name
	 * @param value the group
	 */
    void setGroup(String name, ITreasureChestGroup value);
	
	/**
	 * Returns whether this a group exists with the specified name
	 * @param name the group name
	 * @return true if the group exists, false if it does not
	 */
    boolean hasGroup(String name);
	
	/**
	 * Delete the group with the specified name
	 * @param name the group name
	 * @return true if a group was deleted, false otherwise
	 */
    boolean removeGroup(String name);

	/**
	 * Returns all group names
	 * @return all group names
	 */
    Set<String> getGroupNames();
	
}
