package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * This is just a wrapper class for ItemStack. In case we need to do some custom serialization.
 * 
 * @author Mitch
 *
 */
public class ItemStackWrapper implements ConfigurationSerializable {

	private ItemStack stack;


	public ItemStack getItemStack() {
		return stack;
	}
	
	public void setItemStack(ItemStack stack) {
		if(stack == null) {
			this.stack = new ItemStack(Material.AIR); //Upgrade from material IDs
		}
		else {
			this.stack = stack.clone();
		}
	}
	
	public ItemStackWrapper(ItemStack stack) {
		setItemStack(stack);
	}
	
	public ItemStackWrapper(Map<String, Object> values) {
		setItemStack( (ItemStack) values.get("stack"));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("stack", stack);
		
		return values;
	}
}
