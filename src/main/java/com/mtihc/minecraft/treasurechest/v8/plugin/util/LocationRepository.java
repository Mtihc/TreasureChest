package com.mtihc.minecraft.treasurechest.v8.plugin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class LocationRepository<T extends ConfigurationSerializable> {

	private File directory;

	public LocationRepository(File directory) {
		this.directory = directory;
	}
	
	public LocationRepository(String directory) {
		this(new File(directory));
	}


	public File getDirectory() {
		return directory;
	}
	
	public File getWorldDirectory(String worldName) {
		return new File(directory + "/" + worldName);
	}
	
	private String locationToString(Location location) {
		return location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}
	
	protected String getConfigPath(Location location) {
		return getWorldDirectory(location.getWorld().getName()) + "/" + locationToString(location) + ".yml";
	}
	
	protected File getConfigFile(Location location) {
		return new File(getConfigPath(location));
	}
	
	protected YamlConfiguration getConfig(Location location) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration result = new YamlConfiguration();
		result.load(getConfigFile(location));
		return result;
	}
	
	protected void saveConfig(Location location, T value) throws IOException {
		YamlConfiguration result = new YamlConfiguration();
		result.set("location", value);
		result.save(getConfigFile(location));
	}
	
	@SuppressWarnings("unchecked")
	public T load(Location location) {
		try {
			return (T) getConfig(location).get("location");
		} catch(FileNotFoundException e) {
			return null;
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load value at " + locationToString(location), e);
			return null;
		}
	}
	
	public void save(Location location, T value) {
		if(value == null) {
			return;
		}
		try {
			saveConfig(location, value);
		} catch(IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save value at " + locationToString(location), e);
			return;
		}
	}
	
	public boolean has(Location location) {
		return getConfigFile(location).exists();
	}
	
	public void delete(Location location) {
		getConfigFile(location).delete();
	}
	
	public Set<Location> getLocations(String worldName) {
		
		final Set<Location> result = new HashSet<Location>();
		
		File dir = getWorldDirectory(worldName);
		if(!dir.exists()) {
			Bukkit.getLogger().info("dir " + worldName + " doesn't exist");
			return result;
		}
		Bukkit.getLogger().info("dir " + worldName + " exists");
		final World world = Bukkit.getWorld(worldName);
		if(world == null) {
			Bukkit.getLogger().info("world " + worldName + " doesn't exist");
			return result;
		}
		Bukkit.getLogger().info("world " + worldName + " exists");
		
		dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String[] split = name.split("[_.]");
				if(split.length != 4) {
					return false;
				}
				
				try {
					int x = Integer.parseInt(split[0]);
					int y = Integer.parseInt(split[1]);
					int z = Integer.parseInt(split[2]);
					result.add(new Location(world, x, y, z));
					return false;
				} catch(NumberFormatException e) {
					Bukkit.getLogger().info("error "+ e.getCause().getMessage() + ": " + e.getMessage());
					return false;
				}
				
			}
		});
		return result;
	}
	
	
	
}
