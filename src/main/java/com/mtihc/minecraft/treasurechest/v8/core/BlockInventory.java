package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class BlockInventory implements IBlockInventory {

	private Location location;
	private InventoryType type;
	private int size;
	private ItemStack[] contents;
	
	/**
	 * @deprecated This constructor is only required to convert from v7 to v8
	 */
	@Deprecated
	public BlockInventory(Location location, ItemStack[] contents) {
		this.location = location;
		this.type = InventoryType.CHEST;
		this.contents = contents;
		this.size = Math.min(type.getDefaultSize() * 2, contents.length);
		if(contents.length > size) {
			ItemStack[] newContents = new ItemStack[size];
			int index = 0;
			for (int i = 0; i < contents.length; i++) {
				if(contents[i] == null || contents[i].getTypeId() == 0) {
					continue;
				}
				newContents[index] = contents[i];
				index++;
			}
			this.contents = newContents;
		}
	}

	public BlockInventory(Location location, Inventory inventory) {
		if(inventory instanceof DoubleChestInventory) {
			throw new IllegalArgumentException("Parameter inventory cannot be a DoubleChestInventory.");
		}
		this.location = location;
		this.type = inventory.getType();
		this.size = inventory.getSize();
		this.contents = inventory.getContents();
	}
	
	public BlockInventory(Map<String, Object> values) {
		World world = Bukkit.getWorld((String) values.get("world"));
		Vector coords = (Vector) values.get("coords");
		location = coords.toLocation(world);
		type = InventoryType.valueOf((String) values.get("type"));
		size = (Integer) values.get("size");
		
		contents = new ItemStack[size];
		Map<?, ?> contentsSection = (Map<?, ?>) values.get("contents");
		Set<?> contentsEntries = contentsSection.entrySet();
		int substringBeginIndex = "item".length();
		for (Object object : contentsEntries) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			String key = (String) entry.getKey();
			
			ItemStack item = (ItemStack) entry.getValue();
			int index = Integer.parseInt(key.substring(substringBeginIndex));
			contents[index] = item;
		}
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("world", location.getWorld().getName());
		values.put("coords", location.toVector());
		values.put("type", type.name());
		values.put("size", size);
		
		
		Map<String, Object> contentsSection = new LinkedHashMap<String, Object>();
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if(item == null || item.getTypeId() == 0) {
				continue;
			}
			contentsSection.put("item" + i, item);
		}
		values.put("contents", contentsSection);
		
		return values;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public InventoryType getType() {
		return type;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public ItemStack[] getContents() {
		return contents;
	}

	@Override
	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}

}
