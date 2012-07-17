package com.mtihc.minecraft.treasurechest.v8.core;

import java.io.File;

import com.mtihc.minecraft.treasurechest.v8.plugin.util.LocationRepository;

public class TreasureChestRepository extends LocationRepository<ITreasureChest> implements ITreasureChestRepository {

	public TreasureChestRepository(File directory) {
		super(directory);
	}

	public TreasureChestRepository(String directory) {
		super(directory);
	}

}
