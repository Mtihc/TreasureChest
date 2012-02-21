package com.mtihc.minecraft.treasurechest.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.mtihc.minecraft.treasurechest.ChestNameFormatter;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class ChestExplodeListener implements Listener {

	TreasureChestPlugin plugin;
	
	private ChestNameFormatter chestNameFormatter = null;
	
	
	public ChestExplodeListener(TreasureChestPlugin plugin) {
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
				if(block != null && block.getType().equals(Material.CHEST)) {
					
					String chestName = getChestNameFormatter().getChestName(block);
					TreasureChest treasureChest = plugin.getChests().getChest(chestName);
					
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
