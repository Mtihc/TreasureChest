package com.mtihc.minecraft.treasurechest.v8.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

/**
 * Abstract class that maps yml configurations to any type of key.
 * 
 * <p>The method <code>getYamlFile</code> must be overridden, to convert keys to file paths. 
 * Don't forget to include the root directory. Which is available through variable <code>directory</code>.</p>
 * 
 * @author Mitch
 *
 * @param <K> the type of keys
 */
public abstract class YamlRepository<K> {

	/**
	 * This repository's root directory
	 */
	public final File directory;

	/**
	 * The logger, for save/load errors
	 */
	public final Logger logger;
	
	/**
	 * Constructor.
	 * @param directory this repository's root directory path
	 */
	public YamlRepository(String directory) {
		this(new File(directory));
	}
	
	/**
	 * Constructor.
	 * @param directory this repository's root directory
	 */
	public YamlRepository(File directory) {
		this(directory, null);
	}
	
	/**
	 * Constructor.
	 * @param directory this repository's root directory path
	 * @param logger the logger, for save/load errors
	 */
	public YamlRepository(String directory, Logger logger) {
		this(new File(directory), logger);
	}
	
	/**
	 * Constructor.
	 * @param directory this repository's root directory
	 * @param logger the logger, for save/load errors
	 */
	public YamlRepository(File directory, Logger logger) {
		this.directory = directory;
		if(logger != null) {
			this.logger = logger;
		}
		else {
			this.logger = Bukkit.getLogger();
		}
	}
	
	/**
	 * Returns the yml file.
	 * 
	 * <p>This method converts the specified key, to a file path. 
	 * The file path includes the root directory. 
	 * Which is available through variable <code>diretory</code>.</p>
	 * 
	 * @param key the key
	 * @return the file
	 */
	public abstract File getYamlFile(K key);

	
	/**
	 * Returns whether a yml configuration exists at the specified key
	 * @param key the key
	 * @return true if the file exists, false otherwise
	 */
	public boolean exists(K key) {
		return getYamlFile(key).exists();
	}

	/**
	 * Deletes the yml configuration file at the specified key.
	 * @param key the key
	 * @return true if a yml configuration was deleted, false otherwise
	 */
	public boolean delete(K key) {
		return getYamlFile(key).delete();
	}
    
    /**
     * Save a yml configuration at the specified key.
     * @param key the key
     * @param config the yml configuration
     * @throws IOException thrown when the file could not be saved
     */
	public void saveYamlConfig(K key, YamlConfiguration config) throws IOException {
		File file = getYamlFile(key);
		config.save(file);
	}
	
	/**
	 * Loads the yml configuration at the specified key.
	 * @param key the key
	 * @return the yml configuration
	 * @throws FileNotFoundException thrown when the file was not found
	 * @throws IOException thrown when the file could not be loaded
	 * @throws InvalidConfigurationException thrown when there's invalid yml code
	 */
	public YamlConfiguration loadYamlConfig(K key) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		File file = getYamlFile(key);
		if (isValidUTF8(Files.toByteArray(file))) {
        	config.loadFromString(Files.toString(file, Charset.forName("UTF-8")));
        } else {
        	config.load(file);
        }
        return config;
	}
	

    /**
     * Checks if the given byte array is UTF-8 encoded.
     *
     * @param bytes The array of bytes to check for validity
     * @return true when validly UTF8 encoded
     */
    private static boolean isValidUTF8(byte[] bytes) {
        try {
            Charset.availableCharsets().get("UTF-8").newDecoder().decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }
	
	/**
	 * Returns all yml configuration file names in this repository.
	 * @return all yml configuration file names in this repository.
	 */
	public Set<String> getNames() {
		final HashSet<String> result = new HashSet<String>();
		final String extension = ".yml";
		final int length = extension.length();
		directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				int n = name.length();
				if(name.endsWith(".yml") && n > length) {
					result.add( name.substring(0, n - length) );
					
				}
				return false;
			}
		});
		return result;
	}
}
