package com.mtihc.minecraft.treasurechest.v8.core;

public interface ITreasureDataFacade extends ITreasureChestMemory, ITreasureChestRepository, ITreasureChestGroupRepository {

	/**
	 * Returns the treasure manager configuration object.
	 * @return the treasure manager configuration object.
	 */
	public abstract ITreasureManagerConfiguration getConfig();
	
}
