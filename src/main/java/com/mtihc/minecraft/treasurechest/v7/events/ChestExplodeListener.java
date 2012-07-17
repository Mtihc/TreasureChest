package com.mtihc.minecraft.treasurechest.v7.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class ChestExplodeListener implements Listener {

	TreasureChestPlugin plugin;
	
	public ChestExplodeListener(TreasureChestPlugin plugin) {
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.entity.EntityListener#onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(final EntityExplodeEvent event) {
		List<Block> blockList = event.blockList();
		List<Block> blockListNew = new ArrayList<Block>();
		blockListNew.addAll(blockList);
		int i = 0;
		if(blockList != null) {
			for (Block block : blockList) {
				if(block != null && block.getState() instanceof InventoryHolder) {
					InventoryHolder holder = ((InventoryHolder)block.getState()).getInventory().getHolder();
					if(holder instanceof DoubleChest) {
						block = ((DoubleChest)holder).getLocation().getBlock();
					}
					String id = TChestCollection.getChestId(block.getLocation());
					TreasureChest treasureChest = plugin.getChests().values().getChest(id);
					
					if(treasureChest != null) {
						blockListNew.remove(i);
						// don't increment i
						continue;
					}
				}
				i++;
			}
		}
		blockList.clear();
		blockList.addAll(blockListNew);
	}

	
}
