package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackWrapper implements ConfigurationSerializable {

	private CraftItemStack stack;


	public ItemStack getItemStack() {
		return stack;
	}
	
	public void setItemStack(ItemStack stack) {
		if(stack == null) {
			this.stack = new CraftItemStack(0);
		}
		else if(stack instanceof CraftItemStack) {
			this.stack  = (CraftItemStack) stack;
		}
		else {
			this.stack = new CraftItemStack(stack);
		}
	}
	
	public ItemStackWrapper(ItemStack stack) {
		setItemStack(stack);
	}
	
	public ItemStackWrapper(Map<String, Object> values) {
		setItemStack( (ItemStack) values.get("stack"));
		
		Map<?, ?> extra = (Map<?, ?>) values.get("tag");
		if(extra != null) {
			CraftItemStack craftItem = new CraftItemStack(stack);
			NBTTagCompound tag = new NBTTagCompound("tag");
			if(stack.getType() == Material.WRITTEN_BOOK || stack.getType() == Material.BOOK_AND_QUILL) {
				writtenBook(tag, extra);
				stack = craftItem;
				craftItem.getHandle().setTag(tag);
				
			}
		}
		
		
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("stack", stack);
		
		NBTTagCompound tag = ((CraftItemStack)stack).getHandle().getTag();
		if(tag == null) {
			return values;
		}
		
		Bukkit.getLogger().info("tag name: " + tag.getName());
		
		if(stack.getType() == Material.WRITTEN_BOOK || stack.getType() == Material.BOOK_AND_QUILL) {
			Map<String, Object> extra = new LinkedHashMap<String, Object>();
			
			extra.put("author", tag.getString("author"));
			extra.put("title", tag.getString("title"));
			
			LinkedHashMap<String, Object> pageSection = new LinkedHashMap<String, Object>();
			extra.put("pages", pageSection);
			NBTTagList pages = tag.getList("pages");
			
			int size = pages.size();
			int index = 0;
			while(index < size) {
				NBTTagString page = (NBTTagString) pages.get(index);
				pageSection.put("page" + index, page.data);
				index++;
			}
			
			values.put("tag", extra);
		}
		
		return values;
	}
	
	
	
	

	
	private void writtenBook(NBTTagCompound tag, Map<?, ?> values) {
		tag.setString("author", (String) values.get("author"));
		tag.setString("title", (String) values.get("title"));
		
		NBTTagList pageListTag = new NBTTagList("pages");
		
		Map<?, ?> pageSection = (Map<?, ?>) values.get("pages");
		Collection<?> pageValues = pageSection.values();
		int index = 0;
		for (Object page : pageValues) {
			pageListTag.add(new NBTTagString(String.valueOf(index), page.toString()));
			index++;
		}
		
		tag.set("pages", pageListTag);
	}
}
