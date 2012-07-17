package com.mtihc.minecraft.treasurechest.v8.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;

public class TreasureChestOpenEvent extends TreasureChestEvent {
	
	public TreasureChestOpenEvent(Player player, ITreasureChest tchest,
			Inventory inventory) {
		super(player, tchest, inventory);
	}

	private static final HandlerList handlers = new HandlerList();
	
	
	 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
