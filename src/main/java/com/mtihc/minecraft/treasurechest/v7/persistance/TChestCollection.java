package com.mtihc.minecraft.treasurechest.v7.persistance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class TChestCollection implements ConfigurationSerializable {

	private Map<String, TreasureChest> chests;
	
	public TChestCollection() {
		chests = new HashMap<String, TreasureChest>();
	}
	
	public static String getChestId(Location location) {
		String worldName = location.getWorld().getName().replace(" ", "_");
		String coordinates = location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		return worldName + "_" + coordinates;
	}
	
	
	public Collection<TreasureChest> getChests() {
		return chests.values();
	}
	
	public boolean isEmpty() {
		return chests.isEmpty();
	}
	
	public int getChestTotal() {
		return chests.size();
	}
	
	public int getChestItemTotal(String id) {
		int result = 0;
		TreasureChest chest = chests.get(id);
		for (ItemStack stack : chest.getContents()) {
			if(stack != null) {
				result++;
			}
		}
		return result;
	}
	
	public Collection<String> getChestIds() {
		return chests.keySet();
	}
	
	public TreasureChest getChest(String id) {
		return chests.get(id);
	}
	
	public void setChest(TreasureChest tchest) {
		chests.put(getChestId(tchest.getLocation()), tchest);
	}
	
	public boolean hasChest(String id) {
		return chests.containsKey(id);
	}
	
	public TreasureChest removeChest(String id) {
		return chests.remove(id);
	}
	
	
	public TChestCollection(Map<String, Object> values) {
		this.chests = new HashMap<String, TreasureChest>();
		Map<?, ?> chestSection = (Map<?, ?>) values.get("chests");
		Set<?> entries = chestSection.entrySet();
		for (Object object : entries) {
			Entry<?, ?> entry = (Entry<?, ?>) object;
			this.chests.put(entry.getKey().toString(), (TreasureChest) entry.getValue());
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("chests", chests);
		
		return result;
	}

}
