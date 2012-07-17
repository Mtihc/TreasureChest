package com.mtihc.minecraft.treasurechest.v7;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v7.core.YamlFile;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class Config extends YamlFile {

	public Config(JavaPlugin plugin) {
		super(plugin, "config");
	}

	public boolean getDefaultIgnoreProtection() {
		return getConfig().getBoolean("defaults.ignoreProtection", false);
	}
	
	public String getDefaultMessage(TreasureChest.Message id) {
		if(id.equals(TreasureChest.Message.FOUND)) {
			return getConfig().getString("defaults.messages.found");
		}
		else if(id.equals(TreasureChest.Message.FOUND_ALREADY)) {
			return getConfig().getString("defaults.messages.found_already");
		}
		else if(id.equals(TreasureChest.Message.FOUND_UNLIMITED)) {
			return getConfig().getString("defaults.messages.unlimited");
		}
		else {
			return null;
		}
	}
	
	@Override
	public void reload() {
		super.reload();
	}
}
