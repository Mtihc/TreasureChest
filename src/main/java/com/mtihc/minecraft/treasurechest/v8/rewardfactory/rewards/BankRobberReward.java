package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class BankRobberReward implements IReward {

	private RewardInfo info;
	private BankRobberRewardFactory factory;

	public BankRobberReward(BankRobberRewardFactory factory) {
		this.factory = factory;
		this.info = new RewardInfo("bankrobber", new HashMap<String, Object>());
	}
	
	BankRobberReward(BankRobberRewardFactory factory, RewardInfo info) {
		this.factory = factory;
		this.info = info;
	}
	
	@Override
	public RewardInfo getInfo() {
		return info;
	}

	@Override
	public String getDescription() {
		return factory.getGeneralDescription();
	}

	@Override
	public void give(Player player) throws RewardException {
		
	}

}
