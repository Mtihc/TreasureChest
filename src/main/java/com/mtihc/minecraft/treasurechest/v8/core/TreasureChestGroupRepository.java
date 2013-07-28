package com.mtihc.minecraft.treasurechest.v8.core;

import java.io.File;

import com.mtihc.minecraft.treasurechest.v8.plugin.util.GroupRepository;

public class TreasureChestGroupRepository extends GroupRepository<ITreasureChestGroup> implements ITreasureChestGroupRepository {

	public TreasureChestGroupRepository(File directory) {
		super(directory);
	}

	public TreasureChestGroupRepository(String directory) {
		super(directory);
	}
}
