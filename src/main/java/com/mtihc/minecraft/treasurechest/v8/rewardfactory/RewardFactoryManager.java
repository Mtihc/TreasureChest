package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory.CreateCallback;

public class RewardFactoryManager {
	
	private Map<String, RewardFactory> factories = new LinkedHashMap<String, RewardFactory>();
	
	public RewardFactoryManager() {
		
	}
	
	public boolean hasFactory(String label) {
		return factories.containsKey(label);
	}
	
	public RewardFactory getFactory(String label) {
		return factories.get(label);
	}
	
	public void setFactory(RewardFactory factory) {
		this.factories.put(factory.getLabel(), factory);
	}
	
	public String[] getFactoryLabels() {
		Set<String> keys = factories.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	public int getFactoryTotal() {
		return factories.size();
	}

	
	public IReward create(RewardInfo info) throws RewardException {
		RewardFactory f = getFactory(info.getLabel());
		if(f == null) {
			throw new RewardException("There is no factory for reward type \"" + info.getLabel() + "\".");
		}
		return f.createReward(info);
	}
	
	public void create(CommandSender sender, String label, String[] args, CreateCallback callback) throws RewardException {
		RewardFactory f = factories.get(label);
		if(f == null) {
			throw new RewardException("There is no factory for reward type \"" + label + "\".");
		}
		f.createRewardInfo(sender, args, callback);
	}
	
	
	
}
