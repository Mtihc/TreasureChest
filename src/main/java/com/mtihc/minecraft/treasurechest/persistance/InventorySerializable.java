package com.mtihc.minecraft.treasurechest.persistance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Only required to convert from v7 to v8
 * @author Mitch
 *
 */
@Deprecated
public class InventorySerializable implements ConfigurationSerializable {

	private int size;
	private ItemStack[] items;
	
	public InventorySerializable(Inventory inventory) {
		this.size = inventory.getSize();
		this.items = Arrays.copyOf(inventory.getContents(), inventory.getSize());
	}
	
	public InventorySerializable(int size) {
		this.items = new ItemStack[size];
		this.size = size;
	}
	
	public InventorySerializable(ItemStack[] contents) {
		this.items = contents;
		this.size = contents.length;
	}
	
	
	

	public ItemStack[] getContents() {
		return Arrays.copyOf(items, items.length);
	}
	
	public void setContents(ItemStack[] contents) {
		this.items = Arrays.copyOf(contents, contents.length);
		this.size = contents.length;
	}
	
	public void clearContents(int size) {
		this.items = new ItemStack[size];
		this.size = size;
	}
	
	
	
	public static InventorySerializable deserialize(Map<String, Object> map) {
		return new InventorySerializable(map);
	}
	
	public InventorySerializable(Map<String, Object> map) {
		
		this.size = Integer.parseInt(map.get("size").toString());
		this.items = new ItemStack[this.size];
		
		Map<?, ?> itemSection = (Map<?, ?>) map.get("items");
		Set<?> entries = itemSection.entrySet();
		
		for (Object object : entries) {
			Entry<?, ?> entry = (Entry<?, ?>) object;
			int index = convertItemIdToIndex(entry.getKey().toString());
			this.items[index] = (ItemStack) entry.getValue();
			
		}
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		HashMap<String, Object> itemSection = new HashMap<String, Object>();
		for (int i = 0; i < items.length; i++) {
			if(items[i] == null) {
				continue;
			}
			itemSection.put(convertIndexToItemId(i), items[i]);
		}
		result.put("items", itemSection);
		result.put("size", size);
		
		return result;
	}
	
	private static String convertIndexToItemId(int index) {
		return "item" + index;
	}
	
	private static int convertItemIdToIndex(String itemId) {
		return Integer.parseInt(itemId.substring(("item").length()));
	}

}
