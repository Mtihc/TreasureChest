package com.mtihc.minecraft.treasurechest.v8.events;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;

public abstract class TreasureChestEvent extends Event {

	private ITreasureChest tchest;
 
	protected TreasureChestEvent(ITreasureChest tchest) {
    	this.tchest = tchest;
    }
	
	public ITreasureChest getTreasureChest() {
		return tchest;
	}
	
	public Location getLocation() {
		return tchest.getContainer().getLocation();
	}
}
