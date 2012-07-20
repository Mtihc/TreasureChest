package com.mtihc.minecraft.treasurechest.v8.core;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest.Message;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.YamlFile;

public class TreasureManagerConfiguration extends YamlFile implements
		ITreasureManagerConfiguration {

	public TreasureManagerConfiguration(JavaPlugin plugin, String name) {
		super(plugin, name);
	}

	@Override
	public String getDefaultMessage(Message messageId) {
		return getConfig().getString("defaults.messages." + messageId.name().toLowerCase(), null);
	}

	@Override
	public boolean getDefaultIgnoreProtection() {
		return getConfig().getBoolean("defaults.ignoreProtection");
	}

	public int getSubregionSize() {
		return getConfig().getInt("rewards.restore.subregion-size", 50);
	}

	public int getSubregionTicks() {
		return getConfig().getInt("rewards.restore.subregion-ticks", 10);
	}

}
