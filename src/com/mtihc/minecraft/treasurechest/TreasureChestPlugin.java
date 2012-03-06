package com.mtihc.minecraft.treasurechest;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.events.ChestBreakListener;
import com.mtihc.minecraft.treasurechest.events.ChestExplodeListener;
import com.mtihc.minecraft.treasurechest.events.ChestOpenListener;
import com.mtihc.minecraft.treasurechest.persistance.ChestsYaml;
import com.mtihc.minecraft.treasurechest.persistance.InventorySerializable;
import com.mtihc.minecraft.treasurechest.persistance.MemoryDb;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class TreasureChestPlugin extends JavaPlugin implements ChestNameFormatter {

	
	static {
		ConfigurationSerialization.registerClass(TreasureChest.class, "TreasureChest");
		ConfigurationSerialization.registerClass(InventorySerializable.class);
	}

	private ChestsYaml chests;
	private MemoryDb memory;
	private Config config;
	private TreasureChestCommand command;
	
	
	@Override
	public void onDisable() {
		getLogger().info("disabled.");
	}

	@Override
	public void onEnable() {
		
		config = new Config(this);
		config.reload();
		chests = new ChestsYaml(this);
		chests.reload();
		memory = new MemoryDb(this);
		
		PluginCommand cmd = getCommand("treasurechest");
		command = new TreasureChestCommand(this, cmd.getLabel(), cmd.getAliases());
		
		ChestBreakListener breakListener = new ChestBreakListener(this);
		ChestExplodeListener explodeListener = new ChestExplodeListener(this);
		ChestOpenListener openListener = new ChestOpenListener(this);
		
		getServer().getPluginManager().registerEvents(breakListener, this);
		getServer().getPluginManager().registerEvents(explodeListener, this);
		getServer().getPluginManager().registerEvents(openListener, this);
		
		//TODO remove this when conversion code in TreasureChest class is removed.
		chests.save();
		
		
		
		getLogger().info(getDescription().getVersion() + " enabled.");
	}

	@Override
	public String getChestName(String worldName, int chestX, int chestY, int chestZ) {
		return worldName + "_" + chestX + "_" + chestY + "_" + chestZ;
	}
	
	@Override
	public String getChestName(Block chest) {
		Location loc = chest.getLocation();
		return getChestName(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	@Override
	public Location getChestLocation(String chestName) {
		String[] split = chestName.split("_");
		
		String worldName = split[0];
		World world = getServer().getWorld(worldName);
		if(world == null) {
			return null;
		}
		try {
			return new Location(world,
					Integer.parseInt(split[1]), 
					Integer.parseInt(split[2]), 
					Integer.parseInt(split[3]));
		} catch(NullPointerException e) {
		} catch(IndexOutOfBoundsException e) {
		} catch(NumberFormatException e) {
		}
		return null;
	}

	public ChestsYaml getChests() {
		return chests;
	}
	
	public MemoryDb getMemory() {
		return memory;
	}

	public boolean disableChestAccessProtection() {
		return config.disableChestAccessProtection();
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

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		if(command.getName().equalsIgnoreCase(label) || command.getAliases().contains(label.toLowerCase())) {
			return command.execute(sender, label, args);
		}
		else {
			return false;
		}
	}
	

	public Chest getTargetedChestBlock(Player player) {
		Block block = player.getTargetBlock(null, 5);
		if(block != null && block.getState() instanceof Chest) {
			return (Chest) block.getState();
		}
		else {
			return null;
		}
	}
}
