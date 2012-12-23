package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_4_6.Item;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ItemStackWrapper implements ConfigurationSerializable {

	private CraftItemStack stack;


	public ItemStack getItemStack() {
		return stack;
	}
	
	public void setItemStack(ItemStack stack) {
		if(stack == null) {
			this.stack = CraftItemStack.asNewCraftStack(Item.byId[0]);
		}
		else if(stack instanceof CraftItemStack) {
			this.stack  = (CraftItemStack) stack;
		}
		else {
			this.stack = CraftItemStack.asCraftCopy(stack);
		}
	}
	
	public ItemStackWrapper(ItemStack stack) {
		setItemStack(stack);
	}
	
	public ItemStackWrapper(Map<String, Object> values) {
		setItemStack( (ItemStack) values.get("stack"));
		
		Map<?, ?> extra = (Map<?, ?>) values.get("tag");
		if(extra != null) {
			CraftItemStack craftItem = CraftItemStack.asCraftCopy(stack);
			if(stack.getType() == Material.WRITTEN_BOOK || stack.getType() == Material.BOOK_AND_QUILL) {
				BookMeta meta = (BookMeta)craftItem.getItemMeta();
				writtenBook(meta, extra);
				stack = craftItem;
				craftItem.setItemMeta(meta);
			}
		}
		
		
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("stack", stack);
		
		BookMeta meta = (BookMeta)stack.getItemMeta();
		if (meta == null) {
			return values;
		}
		
		if(stack.getType() == Material.WRITTEN_BOOK || stack.getType() == Material.BOOK_AND_QUILL) {
			Map<String, Object> extra = new LinkedHashMap<String, Object>();
			
			extra.put("author", meta.getAuthor());
			extra.put("title", meta.getTitle());
			
			LinkedHashMap<String, Object> pageSection = new LinkedHashMap<String, Object>();
			extra.put("pages", pageSection);
			List<String> pages = meta.getPages();
			
			int size = pages.size();
			int index = 0;
			while(index < size) {
				pageSection.put("page" + index, pages.get(index));
				index++;
			}
			
			values.put("tag", extra);
		}
		
		return values;
	}
	
	
	
	

	
	private void writtenBook(BookMeta meta, Map<?, ?> values) {
		meta.setAuthor((String) values.get("author"));
		meta.setTitle((String) values.get("title"));
		
		Map<?, ?> pageSection = (Map<?, ?>) values.get("pages");
		ArrayList<String> pages = new ArrayList<String>();
		Collection<?> pageValues = pageSection.values();
		for (Object page : pageValues) {
			pages.add(page.toString());
		}
		
		meta.setPages(pages);
	}
}
