package com.mtihc.minecraft.treasurechest.v8.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mtihc.minecraft.treasurechest.v8.plugin.util.YamlRepository;

public class TreasureChestMemory extends YamlRepository implements ITreasureChestMemory {

	public TreasureChestMemory(String directory) {
		super(directory);
	}

	public TreasureChestMemory(File directory) {
		super(directory);
	}

	public TreasureChestMemory(String directory, Logger logger) {
		super(directory, logger);
	}

	public TreasureChestMemory(File directory, Logger logger) {
		super(directory, logger);
	}

	@Override
	public Collection<Location> getAllPlayerFound(OfflinePlayer player, World world) {
		ArrayList<Location> locs = new ArrayList<Location>();
		YamlConfiguration config = load(player.getName());
		if(config == null) {
			return locs;
		}
		ConfigurationSection worldSection = config.getConfigurationSection(world.getName());
		if(worldSection == null) {
			return locs;
		}
		Set<String> coordKeys = worldSection.getKeys(false);
		for (String coordKey : coordKeys) {
			locs.add(stringToLocation(world.getName(), coordKey));
		}
		return locs;
	}
	
	private String locationToString(Location loc) {
		return loc.getWorld().getName() + "." + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
	
	private Location stringToLocation(String worldName, String key) {
		World world = Bukkit.getWorld(worldName);
		String[] split = key.split("_");
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		int z = Integer.parseInt(split[2]);
		return new Location(world, x, y, z);
	}

	@Override
	public long whenHasPlayerFound(OfflinePlayer player, Location location) {
		YamlConfiguration config = load(player.getName());
		if(config == null) {
			return 0L;
		}
		return config.getLong(locationToString(location), 0);
	}

	@Override
	public boolean hasPlayerFound(OfflinePlayer player, Location location) {
		return whenHasPlayerFound(player, location) > 0;
	}

	@Override
	public void rememberPlayerFound(OfflinePlayer player, Location location) {
		long time = Calendar.getInstance().getTimeInMillis();
		
		YamlConfiguration config = load(player.getName());
		if(config == null) {
			config = new YamlConfiguration();
		}
		config.set(locationToString(location), time);
		save(player.getName(), config);
	}

	@Override
	public void forgetPlayerFound(OfflinePlayer player, Location location) {
		forgetPlayerFound(player.getName(), location);
	}
	
	private void forgetPlayerFound(String playerName, Location location) {
		YamlConfiguration config = load(playerName);
		if(config == null) {
			return;
		}
		config.set(locationToString(location), null);
		save(playerName, config);
	}

	@Override
	public void forgetPlayerFoundAll(OfflinePlayer player, World world) {
		YamlConfiguration config = load(player.getName());
		if(config == null) {
			return;
		}
		config.set(world.getName(), null);
		save(player.getName(), config);
	}

	@Override
	public void forgetChest(Location location) {
		Set<String> names = getNames();
		for (String name : names) {
			// TODO use scheduler or queue or something
			forgetPlayerFound(name, location);
		}
	}

}
