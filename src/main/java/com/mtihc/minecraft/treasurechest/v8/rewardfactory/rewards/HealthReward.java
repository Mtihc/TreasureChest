package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class HealthReward implements IReward {

	private RewardInfo info;

	protected HealthReward(RewardInfo info) {
		this.info = info;
	}
	
	public HealthReward(int health) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("health", health);
		this.info = new RewardInfo("health", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public int getHealth() {
		return (int) info.getData("health");
	}
	
	public void setHealth(int value) {
		this.info.setData("health", value);
	}

	@Override
	public String getDescription() {
		return getHealth() + " health";
	}

	@Override
	public void give(Player player) throws RewardException {
		int health = player.getHealth() + getHealth() * player.getMaxHealth() / 100;
		if(health > player.getMaxHealth()) {
			health = player.getMaxHealth();
		}
		else if(health < 0) {
			health = 0;
		}
		player.setHealth(health);
	}

}
