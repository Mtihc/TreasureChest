package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;


public class TreasureChest implements ITreasureChest {

	
	
	private IBlockInventory container;
	private final Map<Message, String> messages = new HashMap<Message, String>();
	private boolean unlimited;
	private int random;
	private long forgetTime;
	private boolean ignoreProtection;
	
	public TreasureChest(BlockState blockState) {
		if(!(blockState instanceof InventoryHolder)) {
			throw new IllegalArgumentException("Parameter blockState must be an InventoryHolder.");
		}
		InventoryHolder holder = (InventoryHolder) blockState;
		if(holder.getInventory() instanceof DoubleChestInventory) {
			DoubleChest doubleChest = (DoubleChest) holder.getInventory().getHolder();
			container = new DoubleBlockInventory(doubleChest);
		}
		else {
			container = new BlockInventory(blockState.getLocation(), holder.getInventory());
		}
		
		unlimited = false;
		random = 0;
		forgetTime = 0;
		ignoreProtection = false;
	}
	
	public TreasureChest(Map<String, Object> values) {
		// TODO
		container = (IBlockInventory) values.get("container");
		
		Map<?, ?> msgSection = (Map<?, ?>) values.get("messages");
		Set<?> msgEntries = msgSection.entrySet();
		for (Object object : msgEntries) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			messages.put(
					Message.valueOf((String) entry.getKey()), 
					(String) entry.getValue());
		}
		
		unlimited = (Boolean) values.get("unlimited");
		random = (Integer) values.get("random");
		forgetTime = (long) (Integer) values.get("forget-time");
		ignoreProtection = (Boolean) values.get("ignore-protection");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		
		values.put("container", container);
		
		Map<String, Object> msgSection = new LinkedHashMap<String, Object>();
		Set<Map.Entry<Message, String>> msgEntries = messages.entrySet();
		for (Map.Entry<Message, String> entry : msgEntries) {
			msgSection.put(entry.getKey().name(), entry.getValue());
		}
		values.put("messages", msgSection);
		
		values.put("unlimited", unlimited);
		values.put("random", random);
		values.put("forget-time", forgetTime);
		values.put("ignore-protection", ignoreProtection);
		
		return values;
	}

	@Override
	public IBlockInventory getContainer() {
		return container;
	}

	@Override
	public String getMessage(Message id) {
		return messages.get(id);
	}

	@Override
	public void setMessage(Message id, String message) {
		if(message == null) {
			messages.remove(id);
		}
		else {
			messages.put(id, message);
		}
	}

	@Override
	public boolean hasMessage(Message id) {
		return messages.containsKey(id);
	}

	@Override
	public boolean isUnlimited() {
		return unlimited;
	}

	@Override
	public void setUnlimited(boolean value) {
		this.unlimited = value;
	}

	@Override
	public int getAmountOfRandomlyChosenStacks() {
		return random;
	}

	@Override
	public void setAmountOfRandomlyChosenStacks(int value) {
		this.random = value;
	}

	@Override
	public long getForgetTime() {
		return forgetTime;
	}

	@Override
	public void setForgetTime(long value) {
		this.forgetTime = value;
	}

	@Override
	public boolean ignoreProtection() {
		return ignoreProtection;
	}

	@Override
	public void ignoreProtection(boolean value) {
		this.ignoreProtection = value;
	}

	
	
	
	
	
	
	
	
	
}
