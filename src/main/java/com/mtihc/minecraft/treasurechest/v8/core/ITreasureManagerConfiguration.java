package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.List;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest.Message;

/**
 * Interface that represents the configuration of the TreasureManager.
 * 
 * @author Mitch
 *
 */
public interface ITreasureManagerConfiguration {

	/**
	 * Returns the default message for the specified messageId.
	 * <p>These default messages are used for new treasures.</p>
	 * @param messageId enum value that indicates which message
	 * @return the message string
	 */
    String getDefaultMessage(Message messageId);

	/**
	 * Returns the default value for the ignore protection setting.
	 * 
	 * @return true if protection is ignored, false if protected
	 */
    boolean getDefaultIgnoreProtection();

	/**
	 * Returns how wide/high/long the sub-regions are, when we need to iterate over the blocks in a region.
	 * 
	 * <p>Smaller values are recommended for slow servers. Larger values increase speed.</p>
	 * 
	 * @return how wide/high/long the sub-regions are, when we need to iterate over the blocks in a region.
	 */
	int getSubregionSize();

	/**
	 * Returns how many ticks we reserve for each iteration, when we need to iterate over sub-regions.
	 * 
	 * <p>Larger values are recommended for slow servers. Lower values increase speed.</p>
	 * 
	 * @return how many ticks we reserve for each iteration, when we need to iterate over sub-regions.
	 */
	int getSubregionTicks();

	/**
	 * Returns the list of ranks your server knows.
	 * 
	 * <p>This configuration has priority over the list of ranks for a treasure. 
	 * In other words, ranks that are not in this list will not be checked.</p>
	 * 
	 * @return the list of ranks your server knows.
	 */
	List<String> getRanks();

}
