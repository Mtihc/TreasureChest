package com.mtihc.minecraft.treasurechest;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface ChestNameFormatter {


	public String getChestName(String worldName, int chestX, int chestY, int chestZ);
	
	public String getChestName(Block chest);
	
	public Location getChestLocation(String chestName);
}
