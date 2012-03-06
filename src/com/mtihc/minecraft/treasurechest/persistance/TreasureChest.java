package com.mtihc.minecraft.treasurechest.persistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("TreasureChest")
public class TreasureChest implements ConfigurationSerializable {

	
	public static TreasureChest deserialize(Map<String, Object> map) {
		return new TreasureChest(map);
	}
	
	public enum Message {
		ChestFound,
		ChestAlreadyFound,
		ChestIsUnlimited
	}
	
	private ItemStack[] itemStacks;
	private String linkedChest = null;
	
	private Message[] messageEnums = Message.values();
	private String[] messages = new String[messageEnums.length];
	private boolean isPrimary = true;
	private boolean isUnlimited = false;
	private long time;
	
	public TreasureChest(Map<String, Object> map) {
		if(map == null) {
			itemStacks = new ItemStack[0];
			return;
		}
		
		Object link = map.get("link");
		if(link != null) {
			linkedChest = String.valueOf(link);
		}
		
		Object isPrimaryValue = map.get("isPrimary");
		if(isPrimaryValue != null) {
			isPrimary = Boolean.parseBoolean(String.valueOf(isPrimaryValue));
		}
		else {
			isPrimary = false;
		}
		
		
		if(isPrimary) {
			Object isUnlimitedValue = map.get("isUnlimited");
			if(isUnlimitedValue != null) {
				isUnlimited = Boolean.parseBoolean(String.valueOf(isUnlimitedValue));
			}
			else {
				isUnlimited = false;
			}
			
			String forgetTime = (String) map.get("forgetTime");
			if(forgetTime == null) {
				clearForgetTime();
			}
			else {
				long forgetMillisec = Long.parseLong(forgetTime);
				time = forgetMillisec;
			}
			
			Map<?, ?> messageSection = (Map<?, ?>)map.get("messages");
			
			if(messageSection != null && !messageSection.isEmpty()) {
				for (int i = 0; i < messageEnums.length; i++) {
					String msg = (String) messageSection.get(messageEnums[i].toString());
					messages[i] = msg;
				}
			}
			
			
			
			

			//TODO remove try, keep code in second try
			try {
				List<?> itemStackStrings = (List<?>) map.get("items");
				itemStacks = new ItemStack[itemStackStrings.size()];
				int index = 0;
				for (Object object : itemStackStrings) {
					itemStacks[index] = getItemStack(object.toString());
					index++;
				}
			} catch(ClassCastException e) {
				try {
					// this is the current version
					InventorySerializable itemStackSection = (InventorySerializable) map.get("items");
					if(itemStackSection != null) {
						
						itemStacks = itemStackSection.getContents();
					}
					else {
						itemStacks = new ItemStack[0];
					}
					
				} catch(ClassCastException ex) {
					Map<?, ?> itemStackSection = (Map<?, ?>) map.get("items");
					if(itemStackSection != null) {
						Set<?> keys = itemStackSection.keySet();
						ArrayList<ItemStack> itemStackList = new ArrayList<ItemStack>();
						
						int index = 0;
						for (Object key : keys) {
							int itemStackIndex = getItemStackIndex(itemStackSection.get("index"));
							if(itemStackIndex == -1) {
								itemStackIndex = itemStackList.size();
							}
							while(itemStackList.size() < itemStackIndex - 1) {
								itemStackList.add(null);
							}
							ItemStack stack = ItemStack.deserialize(toNormalMap(itemStackSection.get(key)));
							itemStackList.add(itemStackIndex, stack);
							index++;
						}
						
						itemStacks = itemStackList.toArray(new ItemStack[itemStackList.size()]);
					}
					else {
						itemStacks = new ItemStack[0];
					}
				}
			}
		}
		
		
		
		
	}
	
	public boolean hasForgetTime() {
		return time != 0;
	}
	
	public void setForgetTime(long millisecs) {
		time = millisecs;
	}
	
	public void clearForgetTime() {
		time = 0;
	}
	
	public long getForgetTime() {
		return time;
	}
	
	
	public ItemStack[] getItemStacks() {
		return itemStacks;
	}
	
	public void setItemStacks(ItemStack[] value) {
		itemStacks = value;
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		
		if(linkedChest != null) {
			result.put("link", linkedChest);
		}
		
		result.put("isPrimary", isPrimary);
		if(isPrimary) {
			
			InventorySerializable itemsSection = new InventorySerializable(itemStacks);
			result.put("items", itemsSection);
			
			
			result.put("isUnlimited", isUnlimited);
			
			if(hasForgetTime()) {
				result.put("forgetTime", String.valueOf(time));
			}
			
			
			ConfigurationSection messageSection = new YamlConfiguration();
			boolean messageSectionIsEmpty = true;
			for (int i = 0; i < messageEnums.length; i++) {
				if(messages[i] != null) {
					messageSectionIsEmpty = false;
					messageSection.set(messageEnums[i].toString(), messages[i]);
				}
			}
			if(!messageSectionIsEmpty){
				result.put("messages", messageSection);
			}
		}
		
		return result;
	}
	
	public boolean isPrimary() {
		return isPrimary;
	}
	
	public void setPrimary(boolean value) {
		this.isPrimary = value;
	}
	
	public boolean isLinkedChest() {
		return linkedChest != null;
	}
	
	public String getLinkedChest() {
		return linkedChest;
	}
	
	public void setLinkedTo(String primaryChestName) {
		linkedChest = primaryChestName;
	}
	
	public String getMessage(Message name) {
		return messages[name.ordinal()];
	}
	
	public void setMessage(Message name, String message) {
		messages[name.ordinal()] = message;
	}
	
	public boolean isUnlimited() {
		return isUnlimited;
	}
	
	public void setUnlimited(boolean isUnlimited) {
		this.isUnlimited = isUnlimited;
	}
	
	
	

	private int getItemStackIndex(Object itemStackIndex) {
		if(itemStackIndex == null) {
			return -1;
		}
		try {
			return Integer.parseInt(itemStackIndex.toString());
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	private Map<String, Object> toNormalMap(Object object) {
		Map<?, ?> map = (Map<?, ?>)object;
		Set<?> keys = map.keySet();
		Map<String, Object> result = new HashMap<String, Object>();
		for (Object key : keys) {
			result.put(key.toString(), map.get(key));
		}
		return result;
	}
	

	private ItemStack getItemStack(String itemStackString) {

		int typeSeperatorIndex = -1;
		int amountSeperatorIndex = -1;
		int enchantmentOpenerIndex = -1;
		
		// find typeSeperator and hash tag index
		int n = itemStackString.length();
		for (int j = 0; j < n; j++) {
			char c = itemStackString.charAt(j);
			if(c == '%') {
				typeSeperatorIndex = j;
			}
			else if(c == '#') {
				amountSeperatorIndex = j;
				break;
			}
			else if(c == '[') {
				enchantmentOpenerIndex = j;
			}
		}
		
		
		int id = getItemStackID(itemStackString, typeSeperatorIndex, amountSeperatorIndex, enchantmentOpenerIndex);
		int type = getItemStackType(itemStackString, typeSeperatorIndex, amountSeperatorIndex, enchantmentOpenerIndex);
		int amount = getItemStackSize(itemStackString, typeSeperatorIndex, amountSeperatorIndex, enchantmentOpenerIndex);
		
		ItemStack result = new ItemStack(id, amount, (short)0, (byte)type);
		
		return result;
	}
	
	private int getItemStackID(String itemStackString, int typeSeperatorIndex, int amountSeperatorIndex, int enchantmentOpenerIndex) {

		int itemID;
		// find item ID
		// from the first char, to the type seperator
		// or, if there is no type defined, from the first char to the amount seperator
		// or, if there is no amount defined, from the frist char to the last
		try {
			int endIndex;
			if(typeSeperatorIndex != -1) {
				endIndex = typeSeperatorIndex;
			}
			else if(amountSeperatorIndex != -1) {
				endIndex = amountSeperatorIndex;
			}
			else if(enchantmentOpenerIndex != -1) {
				endIndex = enchantmentOpenerIndex;
			}
			else {
				endIndex = itemStackString.length();
			}
			// convert that section of the string, to int
			itemID = Integer.parseInt(itemStackString.substring(0, endIndex));
		}
		catch(IndexOutOfBoundsException e) {
			itemID = -1;
		}
		catch(NumberFormatException e) {
			itemID = -1;
		}
		
		return itemID;
	}
	
	private int getItemStackType(String itemStackString, int typeSeperatorIndex, int amountSeperatorIndex, int enchantmentOpenerIndex) {
		int itemType;
		// find item type
		if(typeSeperatorIndex != -1) {
			// typeSeperator found in string
			try {
				int endIndex;
				if(amountSeperatorIndex != -1) {
					endIndex = amountSeperatorIndex;
				}
				else if(enchantmentOpenerIndex != -1) {
					endIndex = enchantmentOpenerIndex;
				}
				else {
					endIndex = itemStackString.length();
				}
				itemType = Integer.parseInt(itemStackString.substring(typeSeperatorIndex + 1, endIndex));
			}
			catch(IndexOutOfBoundsException e) {
				// no item type defined
				itemType = 0;
			}
			catch(NumberFormatException e) {
				// no item type defined
				itemType = 0;
			}
		}
		else {
			// no typeSeperator found,
			// no item type defined
			itemType = 0;
		}
		return itemType;
	}
	
	private int getItemStackSize(String itemStackString, int typeSeperatorIndex, int amountSeperatorIndex, int enchantmentOpenerIndex) {
		int itemAmount;
		// find item amount
		if(amountSeperatorIndex != -1) {
			// hash tag found in string
			try {
				int endIndex;
				if(enchantmentOpenerIndex != -1) {
					endIndex = enchantmentOpenerIndex;
				}
				else {
					endIndex = itemStackString.length();
				}
				itemAmount = Integer.parseInt(itemStackString.substring(amountSeperatorIndex + 1, endIndex));
			}
			catch(IndexOutOfBoundsException e) {
				// uhm, player only defined the hash tag
				// default to 1 silently
				itemAmount = 1;
			}
			catch(NumberFormatException e) {
				// typed anything but a number, 
				// defaults to 1
				itemAmount = 1;
			}
		}
		else {
			// no hash tag found,
			// silently defaults to 1
			itemAmount = 1;
		}
		return itemAmount;
	}
}
