package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class TreasureChestGroup implements ITreasureChestGroup {

	private String world;
	private String name;
	private List<Location> chests;
	
	public TreasureChestGroup(String world, String name) {
		this.world = world;
		this.name = name;
		
		chests = new ArrayList<Location>();
	}

	public TreasureChestGroup(Map<String, Object> values) {
		chests = new ArrayList<Location>();

		int count, i;
		this.world = (String) values.get("world");
		this.name = (String) values.get("name");
		count = (Integer) values.get("count");

		Map<?, ?> chestsSection = (Map<?, ?>) values.get("chests");
		for (i=0;i<count;i++) {
			Vector coords = (Vector) chestsSection.get("chest" + i);
			Location loc = coords.toLocation(Bukkit.getWorld(world));
			chests.add(loc);
		}
	}
	
	public void addChest(ITreasureChest chest) throws Exception {
		String chestsWorld = chest.getContainer().getLocation().getWorld().getName();
		if (chestsWorld.equals(world)) {
			Location newLoc = chest.getContainer().getLocation();

			if (!chests.contains(newLoc)) {
				chests.add(chest.getContainer().getLocation());
				return;
			}
			
			throw new Exception("Treasure is already in the group.");
		}
		throw new Exception("Treasure (in world " + chestsWorld + ") is not in the same world as the group (" + world + ")");
	}
	
	public boolean removeChest(ITreasureChest chest) throws Exception {
		if (!chests.contains(chest.getContainer().getLocation())) {
			throw new Exception("Treasure is not in the group.");
		}
		return chests.remove(chest.getContainer().getLocation());
	}

	public Set<Location> getLocations() {
		final Set<Location> result = new HashSet<Location>();
		Iterator<Location> i = chests.iterator();
		while(i.hasNext()) {
			result.add(i.next().clone());
		}

		return result;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		Map<String, Object> chestLocs = new LinkedHashMap<String, Object>();
		Iterator<Location> i = chests.iterator();
		int chestNum = 0;
		
		values.put("world", world);
		values.put("name", name);
		values.put("count", chests.size());
		
		while(i.hasNext()) {
			Location c = i.next();
			chestLocs.put("chest" + chestNum, c.toVector());
			chestNum += 1;
		}
		values.put("chests", chestLocs);
		
		return values;
	}
}
