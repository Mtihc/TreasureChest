package com.mtihc.minecraft.treasurechest.v7.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class ChestBreakListener implements Listener {

	TreasureChestPlugin plugin;
	
	public ChestBreakListener(TreasureChestPlugin plugin) {
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockBreak(org.bukkit.event.block.BlockBreakEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(final BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block != null && block.getState() instanceof InventoryHolder) {
			InventoryHolder holder = ((InventoryHolder)block.getState()).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				block = ((DoubleChest)holder).getLocation().getBlock();
			}
			String id = TChestCollection.getChestId(block.getLocation());
			TreasureChest tchest = plugin.getChests().values().getChest(id);
			
			if(tchest != null) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot break a treasure chest.");
				// TODO
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
		Block block = event.getBlock();
		if(block != null && block.getState() instanceof InventoryHolder) {
			InventoryHolder holder = ((InventoryHolder)block.getState()).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				block = ((DoubleChest)holder).getLocation().getBlock();
			}
			String id = TChestCollection.getChestId(block.getLocation());
			TreasureChest treasureChest = plugin.getChests().values().getChest(id);
			
			if(treasureChest != null) {
				event.setCancelled(true);
			}
			
		}
	}

	
	
}
