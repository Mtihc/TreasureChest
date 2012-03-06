package com.mtihc.minecraft.treasurechest.persistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class InventorySerializable implements ConfigurationSerializable {

	private List<ItemStack> contents;
	
	public static InventorySerializable deserialize(Map<String, Object> map) {
		return new InventorySerializable(map);
	}
	
	public InventorySerializable(Map<String, Object> map) {
		contents = new ArrayList<ItemStack>();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			ItemStack stack;
			try {
				stack = (ItemStack) map.get(key);
			} catch(ClassCastException e) {
				continue;
			}
			int index = convertItemIdToIndex(key);
			addNullsTo(contents, index);
			contents.add(stack);
		}
	}
	
	public InventorySerializable(List<ItemStack> contents) {
		this.contents = contents;
	}
	
	public InventorySerializable(ItemStack[] contents) {
		this.contents = new ArrayList<ItemStack>();
		for (ItemStack itemStack : contents) {
			this.contents.add(itemStack);
		}
	}
	

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		int index = 0;
		for (ItemStack stack : contents) {
			if(stack != null) {
				result.put(convertIndexToItemId(index), stack);
			}
			index++;
		}
		return result;
	}
	
	public ItemStack[] getContents() {
		return contents.toArray(new ItemStack[contents.size()]);
	}
	
	public void setContents(ItemStack[] stacks) {
		contents = new ArrayList<ItemStack>();
		for (ItemStack stack : stacks) {
			contents.add(stack);
		}
	}
	
	
	
	
	
	
	private static void addNullsTo(List<ItemStack> list, int toIndex) {
		int i = list.size();
		while(i < toIndex) {
			list.add(null);
			i++;
		}
	}
	
	


	private static String convertIndexToItemId(int index) {
		return "item" + index;
	}
	
	private static int convertItemIdToIndex(String itemId) {
		return Integer.parseInt(itemId.substring(("item").length()));
	}

//	private static HashMap<String, Object> toNormalMap(Object object) {
//		Map<?,?> map;
//		try {
//			map = (Map<?, ?>) object;
//		} catch(ClassCastException e) {
//			return null;
//		}
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		Set<?> entries = map.entrySet();
//		for (Object e : entries) {
//			Entry<?,?> entry = (Entry<?,?>)e;
//			result.put(entry.getKey().toString(), entry.getValue());
//		}
//		return result;
//	}
	
}
