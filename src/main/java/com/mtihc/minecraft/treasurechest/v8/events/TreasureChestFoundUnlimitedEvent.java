package com.mtihc.minecraft.treasurechest.v8.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;

public class TreasureChestFoundUnlimitedEvent extends TreasureChestEvent implements Cancellable {

	private boolean cancelled;
	
	public TreasureChestFoundUnlimitedEvent(Player player, ITreasureChest tchest,
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

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
}
