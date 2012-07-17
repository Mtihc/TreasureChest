package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public interface ITreasureChestMemory {

	Collection<Location> getAllPlayerFound(OfflinePlayer player, World world);
	
	long whenHasPlayerFound(OfflinePlayer player, Location location);
	
	boolean hasPlayerFound(OfflinePlayer player, Location location);
	
	void rememberPlayerFound(OfflinePlayer player, Location location);
	
	void forgetPlayerFound(OfflinePlayer player, Location location);
	
	void forgetPlayerFoundAll(OfflinePlayer player, World world);
	
	void forgetChest(Location location);
}
