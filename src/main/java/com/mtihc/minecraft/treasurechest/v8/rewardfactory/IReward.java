package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import org.bukkit.entity.Player;


public interface IReward {

	public RewardInfo getInfo();
	
	public String getDescription();
	
	public void give(Player player) throws RewardException;
	
}
