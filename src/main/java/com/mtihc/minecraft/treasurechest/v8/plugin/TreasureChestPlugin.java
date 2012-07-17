package com.mtihc.minecraft.treasurechest.v8.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.core.BlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.DoubleBlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChestMemory;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChestRepository;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManagerConfiguration;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.SimpleCommand;

public class TreasureChestPlugin extends JavaPlugin {

	static {
		ConfigurationSerialization.registerClass(TreasureChest.class);
		ConfigurationSerialization.registerClass(BlockInventory.class);
		ConfigurationSerialization.registerClass(DoubleBlockInventory.class);
	}
	
	
	
	private TreasureManager manager;
	private SimpleCommand cmd;
	private TreasureManagerConfiguration config;
	
	/**
	 * 
	 * @return the treasure manager
	 */
	public TreasureManager getManager() {
		return manager;
	}
	
	@Override
	public void onEnable() {
		
		config = new TreasureManagerConfiguration(this, "config");
		config.reload();
		manager = new TreasureManager(this, config, new TreasureChestRepository(getDataFolder() + "/treasure"), new TreasureChestMemory(getDataFolder() + "/players"), Permission.ACCESS_TREASURE.getNode(), Permission.ACCESS_UNLIMITED.getNode());
		cmd = new TreasureChestCommand(manager, null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String lbl = label.toLowerCase();
		if(cmd.getLabel().equals(lbl) || search(cmd.getAliases(), lbl)) {
			try {
				cmd.execute(sender, args);
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			return true;
		}
		else {
			return false;
		}
	}





	private boolean search(String[] array, String string) {
		for (String e : array) {
			if(e.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#getConfig()
	 */
	@Override
	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#reloadConfig()
	 */
	@Override
	public void reloadConfig() {
		config.reload();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#saveConfig()
	 */
	@Override
	public void saveConfig() {
		config.save();
	}
	
	
	
	
}
