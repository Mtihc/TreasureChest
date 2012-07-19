package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class MoneyReward implements IReward {

	private RewardInfo info;
	private MoneyRewardFactory factory;

	protected MoneyReward(MoneyRewardFactory factory, RewardInfo info) {
		this.factory = factory;
		this.info = info;
	}
	
	public MoneyReward(MoneyRewardFactory factory, double money) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("money", money);
		this.factory = factory;
		this.info = new RewardInfo("money", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public double getMoney() {
		return (Double) info.getData("money");
	}
	
	public void setMoney(double value) {
		info.setData("money", value);
	}

	@Override
	public String getDescription() {
		return factory.format(getMoney());
	}

	@Override
	public void give(Player player) throws RewardException {
		factory.depositPlayer(player.getName(), getMoney());
	}

}
