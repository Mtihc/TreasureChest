package com.mtihc.minecraft.treasurechest.events;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mtihc.minecraft.treasurechest.ChestNameFormatter;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class ChestOpenListener implements Listener{

	
	private TreasureChestPlugin plugin;
	
	private ChestNameFormatter chestNameFormatter;
	
	public ChestOpenListener(TreasureChestPlugin plugin) {
		this.plugin = plugin;
		this.chestNameFormatter = plugin;
	}
	

	public ChestNameFormatter getChestNameFormatter() {
		return chestNameFormatter;
	}
	
	public void setChestNameFormatter(ChestNameFormatter chestNameFormatter) {
		this.chestNameFormatter = chestNameFormatter;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		if(block == null || !block.getType().equals(Material.CHEST) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		// right clicked chest
		
		if(event.isCancelled() && !plugin.disableChestAccessProtection()) {
			return;
		}
		
		// not cancelled
		
		if(!plugin.getChests().hasChest(block)) {
			return;
		}
		
		// right clicked treasure chest
		
		// get treasure chest
		String treasureChestName = getChestNameFormatter().getChestName(block);
		TreasureChest treasureChest = plugin.getChests().getChest(treasureChestName);
		Chest treasureChestBlock = (Chest)block.getState();
		
		String primaryChestName = treasureChestName;
		TreasureChest primaryChest = treasureChest;
		Chest primaryChestBlock = treasureChestBlock;
		
		if(treasureChest.isLinkedChest()) {
			if(primaryChest.isPrimary()) {
				treasureChestBlock = plugin.getChests().getNeighbourChestOf(block);
				treasureChestName = getChestNameFormatter().getChestName(treasureChestBlock.getBlock());
				treasureChest = plugin.getChests().getChest(treasureChestName);
			}
			else {
				primaryChestBlock = plugin.getChests().getNeighbourChestOf(block);
				primaryChestName = getChestNameFormatter().getChestName(primaryChestBlock.getBlock());
				primaryChest = plugin.getChests().getChest(primaryChestName);
			}
		}
		
		boolean canceled = false;
		
		if(!allowAccess(event.getPlayer(), primaryChest.isUnlimited())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to access this chest.");
			canceled = true;
		}
		
		// let player open chest
		event.setCancelled(canceled);
		if(canceled) {
			event.setUseInteractedBlock(Result.DENY);
			return;
		}
		event.setUseInteractedBlock(Result.ALLOW);
		
		if(primaryChest.isUnlimited()) {
			// chest is unlimited
			// reset items
			
			setItemsToChest(primaryChest, primaryChestBlock);
			
			if(primaryChest.isLinkedChest() && treasureChestBlock != null) {
				// it's a large chest
				// add items to chest
				setItemsToChest(treasureChest, treasureChestBlock);
			}
			// get message
			String message = primaryChest.getMessage(TreasureChest.Message.ChestIsUnlimited);
			if(message != null) {
				// send message
				event.getPlayer().sendMessage(ChatColor.GOLD + message);
			}
		}
		else {
			long time = plugin.getMemory().whenHasPlayerFound(event.getPlayer().getName(), primaryChestName);
			
			//if(plugin.getMemory().hasPlayerFound(event.getPlayer().getName(), primaryChestName)) {
			if(time != 0 && !hasForgotten(time, primaryChest.getForgetTime())) {
				// already found
				// get message
				String alreadyFoundMessage = primaryChest.getMessage(TreasureChest.Message.ChestAlreadyFound);
				if(alreadyFoundMessage != null) {
					// send message
					event.getPlayer().sendMessage(ChatColor.GOLD + alreadyFoundMessage);
				}
				return;
				
			}
			else {
				// found for the first time
				// remember that this player found it
				plugin.getMemory().rememberPlayerFound(event.getPlayer().getName(), primaryChestName);
				// add items to chest
				addItemsToChest(primaryChest, primaryChestBlock);
				
				if(primaryChest.isLinkedChest()) {
					// it's a large chest
					
					if(treasureChestBlock != null) {
						// remember that this player found it
						plugin.getMemory().rememberPlayerFound(event.getPlayer().getName(), treasureChestName);
						// add items to chest
						addItemsToChest(treasureChest, treasureChestBlock);
					}
				}
				// get found message
				String foundMessage = primaryChest.getMessage(TreasureChest.Message.ChestFound);
				if(foundMessage != null) {
					// send message
					event.getPlayer().sendMessage(ChatColor.GOLD + foundMessage);
				}
				
			}
		}
		
	}
	
	private boolean hasForgotten(long foundTime, long forgetTime) {
		if(foundTime <= 0) {
			return true;
		}
		else if(forgetTime <= 0) {
			return false;
		}
		Calendar now = Calendar.getInstance();
		Calendar forgot = Calendar.getInstance();
		// forgot = foundTime + forgetTime
		forgot.setTimeInMillis(foundTime + forgetTime);
		return now.compareTo(forgot) > 0;
	}
	
	private boolean allowAccess(Player player, boolean isUnlimitedChest) {
		if(isUnlimitedChest) {
			return player.hasPermission(Permission.ACCESS_UNLIMITED.getNode());
		}
		else {
			return player.hasPermission(Permission.ACCESS_TREASURE.getNode());
		}
	}
	
	private void setItemsToChest(TreasureChest treasureChest, Chest chest) {
		// add items to chest
		int size = chest.getInventory().getSize();
		ItemStack[] items = treasureChest.getItemStacks();
		ItemStack[] contents = new ItemStack[size];
		
		for (int i = 0; i < contents.length && i < items.length; i++) {
			contents[i] = items[i];
		}
		
		chest.getInventory().setContents(contents);
	}
	
	private void addItemsToChest(TreasureChest treasureChest, Chest chest) {
		ItemStack[] treasureContents = treasureChest.getItemStacks();
		ItemStack[] existingContents = chest.getInventory().getContents();
		ItemStack[] combinedContents = new ItemStack[chest.getInventory().getSize()];
		//
		// adds the treasure to the chest
		// make sure we don't put too much in the chest
		//
		// never leave out the treasure, 
		// better leave out what was already in the chest
		//
		
		// start by adding treasure
		// if there's too much to put in the chest, the last stacks are left out
		int n = combinedContents.length;
		for (int i = 0; i < n && i < treasureContents.length; i++) {
			combinedContents[i] = treasureContents[i];
		}
		// then add what was already in the chest
		// if there's too much to put in the chest, the last stacks are left out
		int m = combinedContents.length - treasureContents.length;
		for (int j = 0;j < m && j < existingContents.length; j++) {
			combinedContents[j + treasureContents.length] = existingContents[j];
		}
		
		chest.getInventory().setContents(combinedContents);
	}
	
}
