package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;


public class TreasureChest implements ITreasureChest {

	
	
	private IBlockInventory container;
	private final Map<Message, String> messages = new HashMap<Message, String>();
	private boolean unlimited;
	private int random;
	private long forgetTime;
	private boolean ignoreProtection;
	private boolean singleton;
	
	private List<String> ranks;
	
	private List<RewardInfo> rewards = new ArrayList<RewardInfo>();
	
	public TreasureChest(BlockState blockState, boolean singleton) {
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
		this.singleton = singleton;  
		
		ranks = new ArrayList<String>();
	}
	
	public TreasureChest(Map<String, Object> values) {
		
		container = (IBlockInventory) values.get("container");
		
		Map<?, ?> msgSection = (Map<?, ?>) values.get("messages");
		
		if(msgSection != null) {
			Set<?> msgEntries = msgSection.entrySet();
			for (Object object : msgEntries) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
				messages.put(
						Message.valueOf((String) entry.getKey()), 
						(String) entry.getValue());
			}
		}
		
		
		
		this.ranks = castToStringList(values.get("ranks"));
		
		//--------------------------------
		// [BEGIN] pre-customizable ranks
		//--------------------------------
		try {
			String rank = (String) values.get("rank");
			if(rank != null) {
				ranks.add(rank);
			}
		} catch(Exception e) {
			
		}
		//--------------------------------
		// [END] pre-customizable ranks
		//--------------------------------
		
		
		unlimited = (Boolean) values.get("unlimited");
		random = (Integer) values.get("random");
		forgetTime = Long.parseLong(String.valueOf(values.get("forget-time")));
		ignoreProtection = (Boolean) values.get("ignore-protection");
		/* Older chests won't have singleton in their config in which case they aren't */
		try {
			singleton = (Boolean) values.get("singleton");
		} catch (Exception e) {
			singleton = false;
		}
		
		Map<?, ?> rewardSection = (Map<?, ?>) values.get("rewards");
		if(rewardSection != null) {
			Collection<?> rewardsValues = rewardSection.values();
			for (Object object : rewardsValues) {
				rewards.add((RewardInfo) object);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private List<String> castToStringList(Object value) {
		List<String> result;
		try {
			result = (List<String>) value;
		} catch(Exception e) {
			result = null;
		}
		if(result == null) {
			return new ArrayList<String>();
		}
		return result;
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
		values.put("ranks", ranks);
		values.put("unlimited", unlimited);
		values.put("random", random);
		values.put("forget-time", forgetTime);
		values.put("ignore-protection", ignoreProtection);
		values.put("singleton", singleton);
		
		Map<String, Object> rewardSection = new LinkedHashMap<String, Object>();
		int i = 0;
		for (RewardInfo info : rewards) {
			rewardSection.put("reward" + i, info);
			i++;
		}
		values.put("rewards", rewardSection);
		
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


	@Override
	public boolean hasRewards() {
		return rewards != null && !rewards.isEmpty();
	}

	@Override
	public List<RewardInfo> getRewards() {
		return rewards;
	}

	@Override
	public void setRewards(List<RewardInfo> values) {
		this.rewards = values;
		if(this.rewards == null) {
			this.rewards = new ArrayList<RewardInfo>();
		}
	}
	
	@Override
	public int getRewardTotal() {
		return rewards.size();
	}

	@Override
	public List<String> getRanks() {
		return ranks;
	}

	@Override
	public void setRanks(List<String> value) {
		this.ranks = value;
	}
	
	@Override
	public boolean isSingleton() {
		return singleton;
	}
	
	@Override
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
	
	
	
}
