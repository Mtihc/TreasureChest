package com.mtihc.minecraft.treasurechest.v8.plugin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class GroupRepository<T extends ConfigurationSerializable> {

	private File directory;

	public GroupRepository(File directory) {
		this.directory = directory;
	}
	
	public GroupRepository(String directory) {
		this(new File(directory));
	}
	
	protected String groupToString(String name) {
		return directory + "/" + name + ".yml";
	}
	
	protected File getConfigFile(String name) {
		return new File(groupToString(name));
	}

	protected void saveConfig(String groupName) throws IOException {
		YamlConfiguration result = new YamlConfiguration();
		result.save(getConfigFile(groupName));
	}
	
	protected void saveConfig(String groupName, T value) throws IOException {
		YamlConfiguration result = new YamlConfiguration();
		result.set("group", value);
		result.save(getConfigFile(groupName));
	}

	protected YamlConfiguration getConfig(String group) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration result = new YamlConfiguration();
		result.load(getConfigFile(group));
		return result;
	}
	
	public boolean exists(String groupName) {
		return getConfigFile(groupName).exists();
	}
	
	public boolean create(String groupName) {
		try {
			saveConfig(groupName);
		} catch(IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save group " + groupName, e);
			return false;
		}
		return true;
	}
	
	public void destroy(String groupName) {
		getConfigFile(groupName).delete();
	}

	@SuppressWarnings("unchecked")
	public T load(String group) {
		try {
			return (T) getConfig(group).get("group");
		} catch(FileNotFoundException e) {
			return null;
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load value at " + groupToString(group), e);
			return null;
		}
	}
	
	public void save(String name, T value) {
		if(value == null) {
			return;
		}
		try {
			saveConfig(name, value);
		} catch(IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save value at " + groupToString(name), e);
			return;
		}
	}
	
	public void delete(String name) {
		getConfigFile(name).delete();
	}
}
