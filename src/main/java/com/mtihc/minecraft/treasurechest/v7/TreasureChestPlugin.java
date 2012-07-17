package com.mtihc.minecraft.treasurechest.v7;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v7.events.ChestBreakListener;
import com.mtihc.minecraft.treasurechest.v7.events.ChestExplodeListener;
import com.mtihc.minecraft.treasurechest.v7.events.ChestOpenListener;
import com.mtihc.minecraft.treasurechest.v7.persistance.ChestsYaml;
import com.mtihc.minecraft.treasurechest.v7.persistance.InventorySerializable;
import com.mtihc.minecraft.treasurechest.v7.persistance.LocationSerializable;
import com.mtihc.minecraft.treasurechest.v7.persistance.Memory;
import com.mtihc.minecraft.treasurechest.v7.persistance.MemoryYaml;
import com.mtihc.minecraft.treasurechest.v7.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class TreasureChestPlugin extends JavaPlugin {

	
	static {
		ConfigurationSerialization.registerClass(TChestCollection.class);
		ConfigurationSerialization.registerClass(TreasureChest.class);
		ConfigurationSerialization.registerClass(InventorySerializable.class);
		ConfigurationSerialization.registerClass(LocationSerializable.class);
	}

	private ChestsYaml chests;
	private Memory memory;
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
		memory = new MemoryYaml(this, "memory/flatfile/memory");
		((MemoryYaml)memory).reload();
		//memory = new MemoryDb(this, "memory/sqlite/memory");
		
		PluginCommand cmd = getCommand("treasurechest");
		command = new TreasureChestCommand(this, cmd.getLabel(), cmd.getAliases());
		
		ChestBreakListener breakListener = new ChestBreakListener(this);
		ChestExplodeListener explodeListener = new ChestExplodeListener(this);
		ChestOpenListener openListener = new ChestOpenListener(this);
		
		getServer().getPluginManager().registerEvents(breakListener, this);
		getServer().getPluginManager().registerEvents(explodeListener, this);
		getServer().getPluginManager().registerEvents(openListener, this);
		
		getLogger().info(getDescription().getVersion() + " enabled.");
	}

	public ChestsYaml getChests() {
		return chests;
	}
	
	public Memory getMemory() {
		return memory;
	}

	public boolean getDefaultIgnoreProtection() {
		return config.getDefaultIgnoreProtection();
	}
	
	public String getDefaultMessage(TreasureChest.Message id) {
		return config.getDefaultMessage(id);
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
	
	
	
	//TODO
	public Block getTargetedContainerBlock(Player player) {
		HashSet<Byte> invisible = new HashSet<Byte>();
		invisible.add((byte)Material.AIR.getId());
		invisible.add((byte)Material.WATER.getId());
		invisible.add((byte)Material.LAVA.getId());
		invisible.add((byte)Material.LEAVES.getId());
		invisible.add((byte)Material.FIRE.getId());
		invisible.add((byte)Material.GLASS.getId());
		invisible.add((byte)Material.THIN_GLASS.getId());
		
		Block block = player.getTargetBlock(null, 5);
		
		if(block != null && block.getState() instanceof InventoryHolder) {
			
			InventoryHolder holder = (InventoryHolder) block.getState();
			holder = holder.getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				return ((DoubleChest)holder).getLocation().getBlock();
			}
			else {
				return block;
			}
		}
		else {
			return null;
		}
	}
}
