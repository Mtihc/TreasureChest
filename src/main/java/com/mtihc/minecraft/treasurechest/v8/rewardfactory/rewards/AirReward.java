package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class AirReward implements IReward {

	private RewardInfo info;

	protected AirReward(RewardInfo info) {
		this.info = info;
	}
	
	public AirReward(int air) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("air", air);
		this.info = new RewardInfo("air", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public int getAir() {
		return (Integer) info.getData("air");
	}
	
	public void setAir(int value) {
		this.info.setData("air", value);
	}

	@Override
	public String getDescription() {
		return getAir() + " air";
	}

	@Override
	public void give(Player player) throws RewardException {
		int air = player.getRemainingAir() + getAir() * player.getMaximumAir() / 100;
		if(air > player.getMaximumAir()) {
			air = player.getMaximumAir();
		}
		else if(air < 0) {
			air = 0;
		}
		Bukkit.getLogger().info("air " + air + " max " + player.getMaximumAir());
		player.setRemainingAir(air);
	}

}
