package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

/**
 * Interface representing a Treasure Chest
 * 
 * @author Mitch
 *
 */
public interface ITreasureChest extends ConfigurationSerializable {

	/**
	 * Enum representing the types of messages a Treasure Chest can have.
	 * 
	 * @author Mitch
	 *
	 */
	public enum Message {
		FOUND,
		FOUND_ALREADY,
		UNLIMITED;
	}
	
	public IBlockInventory getContainer();
	
	/**
	 * Returns one of the messages
	 * @param id the Message enum value
	 * @return the message string
	 */
	public String getMessage(Message id);
	
	/**
	 * Set one of the messages
	 * @param id the Message enum value
	 * @param message the message string
	 */
	public void setMessage(Message id, String message);
	
	/**
	 * Returns whether one of the messages is defined
	 * @param id the Message enum value
	 * @return whether the message is defined
	 */
	public boolean hasMessage(Message id);
	
	/**
	 * @return the isUnlimited
	 */
	public boolean isUnlimited();

	/**
	 * @param isUnlimited the isUnlimited to set
	 */
	public void setUnlimited(boolean isUnlimited);

	/**
	 * @return the randomness
	 */
	public int getAmountOfRandomlyChosenStacks();

	/**
	 * @param amount the randomness to set
	 */
	public void setAmountOfRandomlyChosenStacks(int amount);

	/**
	 * @return the forgetTime
	 */
	public long getForgetTime();

	/**
	 * @param forgetTime the forgetTime to set
	 */
	public void setForgetTime(long forgetTime);

	/**
	 * 
	 * @return true if third party protection is ignored, false otherwise
	 */
	public boolean ignoreProtection();

	/**
	 * 
	 * @param value whether third party protection is ignored
	 */
	public void ignoreProtection(boolean value);

	boolean hasRewards();
	public List<RewardInfo> getRewards();
	public void setRewards(List<RewardInfo> values);
	public int getRewardTotal();

}
