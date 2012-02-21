package com.mtihc.minecraft.treasurechest.persistance;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;

import com.mtihc.minecraft.core1.YamlFile;
import com.mtihc.minecraft.treasurechest.ChestNameFormatter;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class ChestsYaml extends YamlFile {

	private ChestNameFormatter chestNameFormatter;

	public ChestsYaml(TreasureChestPlugin plugin) {
		super(plugin, "chests");
		this.chestNameFormatter = plugin;
	}

	public ChestNameFormatter getChestNameFormatter() {
		return chestNameFormatter;
	}
	
	public void setChestNameFormatter(ChestNameFormatter chestNameFormatter) {
		this.chestNameFormatter = chestNameFormatter;
	}
	
	
	
	
	public TreasureChest getChest(String chestName) {
		try {
			ConfigurationSection chests = getConfig().getConfigurationSection("chests");
			return (TreasureChest)chests.get(chestName);
		}
		catch(NullPointerException e) {
			return null;
		}
		catch(ClassCastException e) {
			return null;
		}
		
	}
	
	public boolean hasChest(Block block) {
		try {
			ConfigurationSection chests = getConfig().getConfigurationSection("chests");
			return chests.contains(getChestNameFormatter().getChestName(block));
		} catch(NullPointerException e) {
			return false;
		}
		
	}
	
	public void setChest(Chest chest) {
		
		// get configuration section chests
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		if(chests == null) {
			chests = getConfig().createSection("chests");
		}
		// create treasure chest
		String treasureChestName = getChestNameFormatter().getChestName(chest.getBlock());
		TreasureChest treasureChest = createChest(chest);
		// set treasure chest
		chests.set(treasureChestName, treasureChest);
		
		// find neighbour chest (large chests)
		Chest neighbor = getNeighbourChestOf(chest.getBlock());
		
		
		if(neighbor != null) {
			// create a treasure chest entry for the neighbor
			TreasureChest neighborChest = createChest(neighbor);
			
			// Some properties only need to be added once to the config,
			// and other properties are specific for each half of a large chest.
			// So, we make one of the chests primary
			
			// the other chest is primary.
			neighborChest.setPrimary(false);
			// neighbor chest name
			String neighborChestName = getChestNameFormatter().getChestName(neighbor.getBlock());
			
			// link chests
			neighborChest.setLinkedTo(treasureChestName);
			treasureChest.setLinkedTo(neighborChestName);
			chests.set(neighborChestName, neighborChest);
		}
		
		
		save();
	}
	
	public void removeChest(Location location) {
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		String key = getChestNameFormatter().getChestName(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
		if(!hasChest(location.getBlock())) {
			return;
		}
		TreasureChest chest = (TreasureChest)chests.get(key);
		// remove chest from config
		chests.set(key, null);
		
		if(chest.isLinkedChest()) {
			// and remove neighbor too
			chests.set(chest.getLinkedChest(), null);
		}
		
		save();
	}
	
	public int getChestTotal() {
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		return chests.getKeys(false).size();
	}
	
	private TreasureChest createChest(Chest chest) {
		TreasureChest treasureChest = new TreasureChest(null);
		treasureChest.setMessage(TreasureChest.Message.ChestFound, "You found a treasure chest!");
		treasureChest.setMessage(TreasureChest.Message.ChestAlreadyFound, "You have already found this treasure chest.");
		treasureChest.setMessage(TreasureChest.Message.ChestIsUnlimited, "Take as much as you want!");
		treasureChest.setItemStacks(chest.getInventory().getContents());
		return treasureChest;
	}
	
	public String getMessage(TreasureChest.Message messageName, String chestName) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return null;
		}
		
		// get primary chest
		if(!chest.isPrimary()) {
			chest = getChest(chest.getLinkedChest());
		}
		
		// get message from chest
		return chest.getMessage(messageName);
	}
	
	public long getForgetTime(String chestName) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return 0;
		}
		
		// get primary chest
		if(!chest.isPrimary()) {
			chest = getChest(chest.getLinkedChest());
		}
		return chest.getForgetTime();
	}
	
	public void setForgetTime(String chestName, long millisec) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return;
		}
		
		// keep forgetTime in primary chest's section
		if(!chest.isPrimary()) {
			// also change chestName, it's important
			chestName = chest.getLinkedChest();
			chest = getChest(chestName);
			
		}
		// set forget time
		if(millisec == 0) {
			chest.clearForgetTime();
		}
		else {
			chest.setForgetTime(millisec);
		}
		
		// get parent section of chests
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		// update the primary chest, with correct name
		chests.set(chestName, chest);
		save();
	}
	
	public void setMessage(TreasureChest.Message messageName, String chestName, String message) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return;
		}
		
		// keep all messages in primary chest's section
		if(!chest.isPrimary()) {
			// also change chestName, it's important
			chestName = chest.getLinkedChest();
			chest = getChest(chestName);
			
		}
		
		// set message
		chest.setMessage(messageName, message);
		// get parent section of chests
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		// update the primary chest, with correct name
		chests.set(chestName, chest);
		save();
	}
	
	public boolean isUnlimited(String chestName) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return false;
		}
		
		// get primary chest
		if(!chest.isPrimary()) {
			chest = getChest(chest.getLinkedChest());
		}
		
		// get message from chest
		return chest.isUnlimited();
	}
	
	public boolean switchUnlimited(String chestName) {
		TreasureChest chest = getChest(chestName);
		if(chest == null) {
			return false;
		}
		
		// keep unlimited flag in primary chest's section
		if(!chest.isPrimary()) {
			// also change chestName, it's important
			chestName = chest.getLinkedChest();
			chest = getChest(chestName);
		}
		
		// switched flag
		boolean result = !chest.isUnlimited();
		// set isUnlimited
		chest.setUnlimited(result);
		// get parent section of chests
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		// update the primary chest, with correct name
		chests.set(chestName, chest);
		save();
		return result;
	}
	
	public Chest getNeighbourChestOf(Block block) {
		Block block1 = block.getRelative(0, 0, 1);
		Block block2 = block.getRelative(0, 0, -1);
		Block block3 = block.getRelative(1, 0, 0);
		Block block4 = block.getRelative(-1, 0, 0);
		if(isChest(block1)) {
			return (Chest)block1.getState();
		}
		else if(isChest(block2)) {
			return (Chest)block2.getState();
		}
		else if(isChest(block3)) {
			return (Chest)block3.getState();
		}
		else if(isChest(block4)) {
			return (Chest)block4.getState();
		}
		else {
			return null;
		}
	}
	
	private boolean isChest(Block block) {
		return block != null && block.getType() == Material.CHEST;
	}

	public void setItemStacks(Chest chest) {
		// get configuration section chests
		ConfigurationSection chests = getConfig().getConfigurationSection("chests");
		String chestName = chestNameFormatter.getChestName(chest.getBlock());
		// get treasure chest
		TreasureChest tchest = getChest(chestName);
		if(tchest == null) {
			return;
		}
		tchest.setItemStacks(chest.getInventory().getContents());
		// set treasure chest
		chests.set(chestName, tchest);
		
		// find neighbour chest (large chests)
		if(tchest.isLinkedChest()) {
			String neighborName = tchest.getLinkedChest();
			TreasureChest neighbor = getChest(neighborName);
			Location loc = chestNameFormatter.getChestLocation(neighborName);
			if(neighbor != null && loc.getBlock().getState() instanceof Chest) {
				Chest nchest = (Chest) loc.getBlock().getState();
				neighbor.setItemStacks(nchest.getInventory().getContents());
				chests.set(neighborName, neighbor);
				
			}
		}
		
		save();
	}
}
