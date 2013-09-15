package com.mtihc.minecraft.treasurechest.v8.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class Repository<K, V extends ConfigurationSerializable> extends YamlRepository<K> {

	
	
	/**
	 * @param directory
	 * @param logger
	 */
	public Repository(File directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * @param directory
	 */
	public Repository(File directory) {
		super(directory);
	}

	/**
	 * @param directory
	 * @param logger
	 */
	public Repository(String directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * @param directory
	 */
	public Repository(String directory) {
		super(directory);
	}
	
	public String getKeyString() {
		return "object";
	}

	@SuppressWarnings("unchecked")
	public V load(K key) throws IOException, InvalidConfigurationException {
		try {
			return (V) loadYamlConfig(key).get(getKeyString());
		} catch(NullPointerException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public void save(K key, V value) throws IOException {
		if(value == null) {
			return;
		}
		YamlConfiguration config = new YamlConfiguration();
		config.set(getKeyString(), value);
		saveYamlConfig(key, config);
	}
	
}
