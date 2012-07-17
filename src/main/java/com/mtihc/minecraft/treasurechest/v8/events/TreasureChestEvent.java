package com.mtihc.minecraft.treasurechest.v8.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;

public abstract class TreasureChestEvent extends Event {

	private Player player;
	private ITreasureChest tchest;
	private Inventory inventory;
 
	public TreasureChestEvent(Player player, ITreasureChest tchest, Inventory inventory) {
    	this.player = player;
    	this.tchest = tchest;
    	this.inventory = inventory;
    }
	
	public Player getPlayer() {
		return player;
	}
	
	public ITreasureChest getTreasureChest() {
		return tchest;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
}
