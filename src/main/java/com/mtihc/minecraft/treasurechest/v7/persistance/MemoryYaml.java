package com.mtihc.minecraft.treasurechest.v7.persistance;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v7.core.YamlFile;

public class MemoryYaml extends YamlFile implements Memory {

	public MemoryYaml(JavaPlugin plugin, String name) {
		super(plugin, name);
	}
	
	protected ConfigurationSection player(String playerName) {
		return getConfig().getConfigurationSection(playerName);
	}

	@Override
	public Collection<String> getAllPlayerFound(OfflinePlayer player) {
		return player(player.getName()).getKeys(false);
	}

	@Override
	public long whenHasPlayerFound(OfflinePlayer player, String chestId) {
		return getConfig().getLong(player.getName() + "." + chestId, 0);
	}

	@Override
	public boolean hasPlayerFound(OfflinePlayer player, String chestId) {
		return whenHasPlayerFound(player, chestId) > 0;
	}

	@Override
	public void rememberPlayerFound(OfflinePlayer player, String chestId) {
		long time = Calendar.getInstance().getTimeInMillis();
		getConfig().set(player.getName() + "." + chestId, time);
		save();
	}

	@Override
	public void forgetPlayerFound(OfflinePlayer player, String chestId) {
		getConfig().set(player.getName() + "." + chestId, null);
		save();
	}

	@Override
	public void forgetPlayerFoundAll(OfflinePlayer player) {
		getConfig().set(player.getName(), null);
		save();
	}

	@Override
	public void forgetChest(String chestId) {
		Set<String> keys = getConfig().getKeys(false);
		for (String key : keys) {
			getConfig().set(key + "." + chestId, null);
		}
		save();
	}

}
