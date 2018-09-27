package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import org.bukkit.entity.Player;

/**
 * Class that represents a reward. You can give this to a player.
 * 
 * @author Mitch
 *
 */
public interface IReward {

	/**
	 * The serializable reward info
	 * @return the reward info
	 */
    RewardInfo getInfo();
	
	/**
	 * Returns a description of this reward in the current state
	 * @return a description of this reward in the current state
	 */
    String getDescription();
	
	/**
	 * Give this reward to the specified online player.
	 * @param player the online player
	 * @throws RewardException thrown when the reward could not be given to the player
	 */
    void give(Player player) throws RewardException;
	
}
