package com.mtihc.minecraft.treasurechest;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.core1.YamlFile;

public class Config extends YamlFile {

	public Config(JavaPlugin plugin) {
		super(plugin, "config");
	}

	public boolean disableChestAccessProtection() {
		return getConfig().getBoolean("disable_chest_access_protection", false);
	}
	
	public void disableChestAccessProtection(boolean value) {
		getConfig().set("disable_chest_access_protection", value);
		save();
	}
}
