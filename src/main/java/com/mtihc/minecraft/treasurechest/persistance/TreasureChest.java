package com.mtihc.minecraft.treasurechest.persistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TreasureChest implements ConfigurationSerializable {


	public enum Message {
		FOUND,
		FOUND_ALREADY,
		FOUND_UNLIMITED;
	}
	
	
	private ItemStack[] inventory;
	private Location location;
	
	private Map<Message, String> messages;
	private boolean isUnlimited = false;
	private int randomAmount = 0;
	private long forgetTime = 0;
	private boolean ignoreProtection = false;
	
	public TreasureChest(Block block) throws IllegalArgumentException {
		if(!(block.getState() instanceof InventoryHolder)) {
			throw new IllegalArgumentException("Parameter block must have a state of type InventoryHolder.");
		}
		this.location = block.getLocation();
		this.inventory = ((InventoryHolder) block.getState()).getInventory().getContents();
		this.messages = new HashMap<TreasureChest.Message, String>();
	}
	

	public TreasureChest(Location location, ItemStack[] inventory) {
		this.location = location;
		this.inventory = inventory;
		this.messages = new HashMap<TreasureChest.Message, String>();
	}
	
	public String getMessage(Message id) {
		return messages.get(id);
	}
	
	public void setMessage(Message id, String message) {
		if(message == null) {
			messages.remove(id);
		}
		else {
			messages.put(id, message);
		}
	}
	
	public boolean hasMessage(Message id) {
		return messages.containsKey(id);
	}
	
	public ItemStack[] getContents() {
		return inventory;
	}
	
	public void setContents(ItemStack[] contents) {
		inventory = contents;
	}
	
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the isUnlimited
	 */
	public boolean isUnlimited() {
		return isUnlimited;
	}

	/**
	 * @param isUnlimited the isUnlimited to set
	 */
	public void setUnlimited(boolean isUnlimited) {
		this.isUnlimited = isUnlimited;
	}

	/**
	 * @return the randomness
	 */
	public int getAmountOfRandomlyChosenStacks() {
		return randomAmount;
	}

	/**
	 * @param amount the randomness to set
	 */
	public void setAmountOfRandomlyChosenStacks(int amount) {
		this.randomAmount = amount;
	}

	/**
	 * @return the forgetTime
	 */
	public long getForgetTime() {
		return forgetTime;
	}

	/**
	 * @param forgetTime the forgetTime to set
	 */
	public void setForgetTime(long forgetTime) {
		this.forgetTime = forgetTime;
	}

	public boolean ignoreProtection() {
		return this.ignoreProtection ;
	}

	public void ignoreProtection(boolean value) {
		this.ignoreProtection = value;
	}
	
	public static TreasureChest deserialize(Map<String, Object> values) {
		LocationSerializable location = (LocationSerializable)values.get("location");
		InventorySerializable inventory = (InventorySerializable)values.get("inventory");
		
		TreasureChest result = new TreasureChest(location.getLocation(), inventory.getContents());
		result.isUnlimited = Boolean.parseBoolean(values.get("isUnlimited").toString());
		result.randomAmount = Integer.parseInt(values.get("randomness").toString());
		result.forgetTime = Long.parseLong(values.get("forgetTime").toString());
		result.ignoreProtection = Boolean.parseBoolean(values.get("ignoreProtection").toString());
		
		Map<?, ?> messageSection = (Map<?, ?>)values.get("messages");
		Set<?> entries = messageSection.entrySet();
		for (Object object : entries) {
			Entry<?, ?> entry = (Entry<?, ?>) object;
			Message id = Message.valueOf(entry.getKey().toString());
			result.setMessage(id, entry.getValue().toString());
		}
		
		return result;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("location", new LocationSerializable(location));
		result.put("inventory", new InventorySerializable(inventory));
		result.put("isUnlimited", isUnlimited);
		result.put("randomness", randomAmount);
		result.put("forgetTime", forgetTime);
		result.put("ignoreProtection", ignoreProtection);
		
		HashMap<String, String> messageSection = new HashMap<String, String>();
		result.put("messages", messageSection);
		Set<Entry<Message, String>> entries = messages.entrySet();
		for (Entry<Message, String> entry : entries) {
			messageSection.put(entry.getKey().name(), entry.getValue());
		}
		
		return result;
	}
	
	private ItemStack[] getRandomizedInventory(ItemStack[] inventory, int randomAmount) {
		
		// copy inventory to result
		ItemStack[] result = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			result[i] = inventory[i];
		}
		
		if(randomAmount < 1) {
			return result;
		}
		
		// find indices of non-empty inventory slots
		List<Integer> nonNulls = new ArrayList<Integer>();
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] != null) {
				nonNulls.add(i);
			}
		}
		
		
		
		Random random = new Random(System.currentTimeMillis());
		int i = randomAmount;
		
		// select random item stacks
		while(i > 0 && nonNulls.size() > 0) {
			int index = random.nextInt(nonNulls.size());
			nonNulls.remove(index);
			i--;
		}
		
		// remove itemstacks, that were not randomly selected, from the result array
		for (Integer integer : nonNulls) {
			result[integer] = null;
		}
		
		
		return result;
		 
	}

	public void toInventoryHolder(InventoryHolder holder) {
		
		ItemStack[] tchest = getRandomizedInventory(inventory, randomAmount);
		int size = tchest.length;
		int sizeTarget = holder.getInventory().getSize();
		
		ItemStack[] result = new ItemStack[sizeTarget];
		
		if(sizeTarget < size) {
			// forget about positions, just add as many as possible
			int index = 0;
			for (ItemStack itemStack : tchest) {
				if(itemStack == null) {
					continue;
				}
				// just add a non-null ItemStack at the next index
				result[index] = itemStack;
				index++;
			}
		}
		else {
			// add items at correct positions
			for (int i = 0; i < tchest.length; i++) {
				result[i] = tchest[i];
			}
		}
		
		holder.getInventory().setContents(result);
		
	}

	
}
