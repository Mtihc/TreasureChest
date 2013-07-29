package com.mtihc.minecraft.treasurechest.v8.plugin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class YamlRepository {

	private File directory;

	public Logger logger;
	
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
	
	
	public File getDirectory() {
		return directory;
	}
	
	public File getYamlFile(String name) {
		return new File(directory + "/" + name + ".yml");
	}
	
	public YamlConfiguration load(String name) {
		YamlConfiguration config = new YamlConfiguration();
		File file = getYamlFile(name);
        try {
            if (isValidUTF8(Files.toByteArray(file))) {
            	config.loadFromString(Files.toString(file, Charset.forName("UTF-8")));
            } else {
            	config.load(file);
            }
        } catch(FileNotFoundException e) {
        	return null;
        } catch (Exception e) {
        	logger.log(Level.WARNING, "Couldn't load \"" + file + "\". ", e);
        	return null;
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

	public void save(String name, YamlConfiguration config) {
		try {
			config.save(getYamlFile(name));
		} catch (IOException e) {
			logger.log(Level.WARNING, "Couldn't save \"" + name + "\".", e);
			return;
		}
	}
	
	public boolean has(String name) {
		return getYamlFile(name).exists();
	}
	
	public Set<String> getNames() {
		final HashSet<String> result = new HashSet<String>();
		final String extension = ".yml";
		final int length = extension.length();
		getDirectory().list(new FilenameFilter() {
			
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

	public void delete(String name) {
		getYamlFile(name).delete();
	}
}
