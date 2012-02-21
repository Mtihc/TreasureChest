package com.mtihc.minecraft.treasurechest.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;

import com.mtihc.minecraft.treasurechest.ChestNameFormatter;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class ChestBreakListener implements Listener {

	TreasureChestPlugin plugin;
	

	private ChestNameFormatter chestNameFormatter = null;
	
	
	public ChestBreakListener(TreasureChestPlugin plugin) {
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
	 * @see org.bukkit.event.block.BlockListener#onBlockBreak(org.bukkit.event.block.BlockBreakEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(final BlockBreakEvent event) {
		if(event.getBlock().getType().equals(Material.CHEST)) {
			
			String chestName = getChestNameFormatter().getChestName(event.getBlock());
			TreasureChest treasureChest = plugin.getChests().getChest(chestName);
			
			if(treasureChest != null) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot break a treasure chest.");
				//TODO
				// if has permission for delete command,
				// inform about: try again after typing: /hiddentreasure delete
			}
			
		}
		
	}


	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockIgnite(org.bukkit.event.block.BlockIgniteEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(final BlockIgniteEvent event) {
		// fire protection
		if(event.getBlock().getType().equals(Material.CHEST)) {
			
			String chestName = getChestNameFormatter().getChestName(event.getBlock());
			TreasureChest treasureChest = plugin.getChests().getChest(chestName);
			
			if(treasureChest != null) {
				event.setCancelled(true);
			}
			
		}
	}

	
	
}
