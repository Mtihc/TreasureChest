package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class LevelReward implements IReward {

	private RewardInfo info;

	public LevelReward(RewardInfo info) {
		this.info = info;
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public int getLevels() {
		return (Integer) info.getData("levels");
	}
	
	public void setLevels(int value) {
		info.setData("levels", value);
	}

	@Override
	public String getDescription() {
		return getLevels() + " levels";
	}

	@Override
	public void give(Player player) throws RewardException {
		player.setLevel(player.getLevel() + getLevels());
	}

}
