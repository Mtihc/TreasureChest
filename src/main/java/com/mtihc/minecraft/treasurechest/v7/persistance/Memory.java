package com.mtihc.minecraft.treasurechest.v7.persistance;

import java.util.Collection;

import org.bukkit.OfflinePlayer;

public interface Memory {
	Collection<String> getAllPlayerFound(OfflinePlayer player);
	
	long whenHasPlayerFound(OfflinePlayer player, String chestId);
	
	boolean hasPlayerFound(OfflinePlayer player, String chestId);
	
	void rememberPlayerFound(OfflinePlayer player, String chestId);
	
	void forgetPlayerFound(OfflinePlayer player, String chestId);
	
	void forgetPlayerFoundAll(OfflinePlayer player);
	
	void forgetChest(String chestId);
}
