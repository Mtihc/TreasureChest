package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

/**
 * Interface representing a treasure container block
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
    enum Message {
		FOUND,
		FOUND_ALREADY,
		UNLIMITED;
	}
	
	/**
	 * Returns the interface that represents the actual block and inventory holder
	 * @return the interface that represents the actual block and inventory holder
	 */
    IBlockInventory getContainer();
	
	/**
	 * Returns one of the messages
	 * @param id the Message enum value
	 * @return the message string
	 */
    String getMessage(Message id);
	
	/**
	 * Set one of the messages
	 * @param id the Message enum value
	 * @param message the message string
	 */
    void setMessage(Message id, String message);
	
	/**
	 * Get the ranks that can access this treasure
	 * 
	 * @return the list of rank names
	 */
    List<String> getRanks();
	
	/**
	 * Set the ranks that can access this treasure
	 * 
	 * @param ranks the list of rank names
	 */
    void setRanks(List<String> ranks);
	
	/**
	 * Returns whether one of the messages is defined
	 * @param id the Message enum value
	 * @return whether the message is defined
	 */
    boolean hasMessage(Message id);
	
	/**
	 * @return the isUnlimited
	 */
    boolean isUnlimited();

	/**
	 * @param isUnlimited the isUnlimited to set
	 */
    void setUnlimited(boolean isUnlimited);

	/**
	 * @return the randomness
	 */
    int getAmountOfRandomlyChosenStacks();

	/**
	 * @param amount the randomness to set
	 */
    void setAmountOfRandomlyChosenStacks(int amount);

	/**
	 * Returns the forget time in milliseconds
	 * <p>Players can access the treasure after this amount of time has passed since last time.</p>
	 * @return the forget time in milliseconds
	 */
    long getForgetTime();

	/**
	 * Set the forget time.
	 * <p>Players can access the treasure after this amount of time has passed since last time.</p>
	 * @param forgetTime the forget time in milliseconds
	 */
    void setForgetTime(long forgetTime);

	/**
	 * Returns whether third part protection is ignored
	 * @return true if third party protection is ignored, false if protected
	 */
    boolean ignoreProtection();

	/**
	 * Set whether third party protection is ignored
	 * @param value whether third party protection is ignored
	 */
    void ignoreProtection(boolean value);

	/**
	 * Returns whether this treasure has any rewards
	 * @return true if this treasure has any rewards, false otherwise
	 */
	boolean hasRewards();
	
	/**
	 * Returns the list of serializable reward info
	 * @return the list of serializable reward info
	 */
    List<RewardInfo> getRewards();
	
	/**
	 * Set the list of reward info
	 * @param values the list of reward info
	 */
    void setRewards(List<RewardInfo> values);
	
	/**
	 * Returns the amount of rewards this treasure has
	 * @return the amount of rewards this treasure has
	 */
    int getRewardTotal();
	
	/**
	 * Returns whether this treasure uses a shared inventory.
	 * 
	 * <p>Shared treasures don't use individual inventories per player. 
	 * Instead, they function like normal chests. 
	 * So the rule "first come, first serve" applies.</p>
	 * 
	 * @return true if this treasure uses a shared inventory, false if it does not
	 */
	boolean isShared();
	
	/**
	 * Set whether this treasure uses a shared inventory.
	 * 
	 * <p>Shared treasures don't use individual inventories per player. 
	 * Instead, they function like normal chests. 
	 * So the rule "first come, first serve" applies.</p>
	 * 
	 * @param value true if this treasure should use a shared inventory, false if it does not
	 */
	void setShared(boolean value);
}
