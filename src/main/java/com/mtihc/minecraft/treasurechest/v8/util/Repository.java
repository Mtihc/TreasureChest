package com.mtihc.minecraft.treasurechest.v8.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
//removed unused import

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Abstract class representing a directory that contains yml files. 
 * Each yml file contains one object of the specified subclass. 
 * 
 * @author Mitch
 *
 * @param <K> the type of keys that will be used to locate yml files
 * @param <V> the type of ConfigurationSerializable that will be saved in each file
 */
public abstract class Repository<K, V extends ConfigurationSerializable> extends YamlRepository<K> {

	
	
	/**
	 * Constructor.
	 * @param directory the root directory of this repository
	 * @param logger the logger, for save/load errors
	 */
	public Repository(File directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * Constructor.
	 * @param directory the root directory of this repository
	 */
	public Repository(File directory) {
		super(directory);
	}

	/**
	 * Constructor.
	 * @param directory the root directory path of this repository
	 * @param logger the logger, for save/load errors
	 */
	public Repository(String directory, Logger logger) {
		super(directory, logger);
	}

	/**
	 * Constructor.
	 * @param directory the root directory path of this repository
	 */
	public Repository(String directory) {
		super(directory);
	}
	
	/**
	 * The string that will be used as key for the serialized object inside the yml file.
	 * @return the key string inside the yml file
	 */
	public String getKeyString() {
		return "object";
	}

	/**
	 * Load a serializable object at the specified key
	 * @param key the key
	 * @return the loaded object
	 * @throws IOException thrown when the file could not be loaded
	 * @throws InvalidConfigurationException thrown when there's invalid yml code
	 */
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
	
	/**
	 * Constructor.
	 * @param key the key
	 * @param value the serializable object
	 * @throws IOException thrown when the file could not be saved
	 */
	public void save(K key, V value) throws IOException {
		if(value == null) {
			return;
		}
		YamlConfiguration config = new YamlConfiguration();
		config.set(getKeyString(), value);
		saveYamlConfig(key, config);
	}
	
}
