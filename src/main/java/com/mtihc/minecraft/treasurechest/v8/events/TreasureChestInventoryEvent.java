package com.mtihc.minecraft.treasurechest.v8.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;

public abstract class TreasureChestInventoryEvent extends TreasureChestEvent {
	
	private Player player;
	private Inventory inventory;

	protected TreasureChestInventoryEvent(ITreasureChest tchest,
			Player player, Inventory inventory) {
		super(tchest);
		this.player = player;
		this.inventory = inventory;
	}

	public Player getPlayer() {
		return player;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
}
