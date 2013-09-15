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

public abstract class YamlRepository<K> {

	public final File directory;

	public final Logger logger;
	
	public YamlRepository(String directory) {
		this(new File(directory));
	}
	
	public YamlRepository(File directory) {
		this(directory, null);
	}
	
	public YamlRepository(String directory, Logger logger) {
		this(new File(directory), logger);
	}
	
	public YamlRepository(File directory, Logger logger) {
		this.directory = directory;
		if(logger != null) {
			this.logger = logger;
		}
		else {
			this.logger = Bukkit.getLogger();
		}
	}
	
	public abstract File getYamlFile(K key);
	
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

	public void saveYamlConfig(K key, YamlConfiguration config) throws IOException {
		File file = getYamlFile(key);
		config.save(file);
	}
	
	public boolean exists(K key) {
		return getYamlFile(key).exists();
	}
	
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

	public boolean delete(K key) {
		return getYamlFile(key).delete();
	}
}
