package com.mtihc.minecraft.treasurechest.v8.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;

import com.mtihc.minecraft.treasurechest.v8.util.Repository;

/**
 * Class representing a folder of yml files containing treasure data.
 * 
 * @author Mitch
 *
 */
public class TreasureChestRepository extends Repository<Location, ITreasureChest> implements ITreasureChestRepository {

	/**
	 * Constructor.
	 * @param directory the treasure directory
	 * @param logger the logger, for save/load errors
	 */
	public TreasureChestRepository(File directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * Constructor.
	 * @param directory the treasure directory
	 */
	public TreasureChestRepository(File directory) {
		super(directory);
	}

	/**
	 * Constructor.
	 * @param directory the treasure directory path
	 * @param logger the logger, for save/load errors
	 */
	public TreasureChestRepository(String directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * Constructor.
	 * @param directory the treasure directory path
	 */
	public TreasureChestRepository(String directory) {
		super(directory);
	}
	
	public File getWorldDirectory(String worldName) {
		return new File(directory + "/" + worldName);
	}
	
	public String locationToString(Location location) {
		return location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}

	@Override
	public File getYamlFile(Location location) {
		return new File(getWorldDirectory(location.getWorld().getName()) + "/"  + locationToString(location) + ".yml");
	}
	
	@Override
	public String getKeyString() {
		return "location";
	}
	

	@Override
	public ITreasureChest getTreasure(Location location) {
		try {
			return load(location);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load Treasure data from file \"" + getYamlFile(location) + "\" due to IOException.", e);
			return null;
		} catch (InvalidConfigurationException e) {
			logger.log(Level.WARNING, "Failed to load Treasure data from file \"" + getYamlFile(location) + "\" due to InvalidConfigurationException.", e);
			return null;
		}
	}

	@Override
	public void setTreasure(ITreasureChest value) {
		if(value == null) return;
		Location location = value.getContainer().getLocation();
		try {
			save(location, value);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to save Treasure data to file \"" + getYamlFile(location) + "\" due to IOException.", e);
		}
	}

	@Override
	public boolean hasTreasure(Location location) {
		return exists(location);
	}

	@Override
	public boolean removeTreasure(Location location) {
		return delete(location);
	}


	@Override
	public Set<Location> getTreasureLocations(String worldName) {
		final Set<Location> result = new HashSet<Location>();
		
		File dir = getWorldDirectory(worldName);
		if(!dir.exists()) {
			logger.log(Level.WARNING, "TreasureChest world directory not found: " + dir);
			return result;
		}
		final World world = Bukkit.getWorld(worldName);
		if(world == null) {
			logger.log(Level.WARNING, "TreasureChest directory found. But world \"" + worldName + "\" doesn't exist.");
			return result;
		}
		
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
					logger.log(Level.WARNING, "Invalid TreasureChest file name \"" + name + "\".", e);
					return false;
				}
				
			}
		});
		return result;
	}
	

}
