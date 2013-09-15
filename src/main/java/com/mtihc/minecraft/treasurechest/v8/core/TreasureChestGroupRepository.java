package com.mtihc.minecraft.treasurechest.v8.core;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;

import com.mtihc.minecraft.treasurechest.v8.plugin.util.Repository;

public class TreasureChestGroupRepository extends Repository<String, ITreasureChestGroup> implements ITreasureChestGroupRepository {

	/**
	 * @param directory
	 * @param logger
	 */
	public TreasureChestGroupRepository(File directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * @param directory
	 */
	public TreasureChestGroupRepository(File directory) {
		super(directory);
	}

	/**
	 * @param directory
	 * @param logger
	 */
	public TreasureChestGroupRepository(String directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * @param directory
	 */
	public TreasureChestGroupRepository(String directory) {
		super(directory);
	}

	@Override
	public File getYamlFile(String key) {
		return new File(directory + "/" + key + ".yml");
	}

	@Override
	public ITreasureChestGroup getGroup(String name) {
		try {
			return load(name);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load Treasure Group from file \"" + getYamlFile(name) + "\" due to IOException.", e);
			return null;
		} catch (InvalidConfigurationException e) {
			logger.log(Level.WARNING, "Failed to load Treasure Group from file \"" + getYamlFile(name) + "\" due to InvalidConfigurationException..", e);
			return null;
		}
	}

	@Override
	public void setGroup(String name, ITreasureChestGroup value) {
		try {
			save(name, value);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to save Treasure Group to file \"" + getYamlFile(name) + "\" due to IOException.", e);
		}
	}

	@Override
	public boolean hasGroup(String name) {
		return exists(name);
	}

	@Override
	public boolean removeGroup(String name) {
		return delete(name);
	}

	@Override
	public Set<String> getGroupNames() {
		return getNames();
	}

	
}
