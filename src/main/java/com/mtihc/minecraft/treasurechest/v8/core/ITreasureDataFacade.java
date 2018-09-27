package com.mtihc.minecraft.treasurechest.v8.core;

/**
 * Facade interface that combines a bunch of data related interfaces.
 * 
 * @author Mitch
 *
 */
public interface ITreasureDataFacade extends ITreasureChestMemory, ITreasureChestRepository, ITreasureChestGroupRepository {

	/**
	 * Returns the treasure manager configuration object.
	 * @return the treasure manager configuration object.
	 */
    ITreasureManagerConfiguration getConfig();
	
}
