package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class FoodReward implements IReward {

	private RewardInfo info;

	protected FoodReward(RewardInfo info) {
		this.info = info;
	}
	
	public FoodReward(int food) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("food", food);
		this.info = new RewardInfo("food", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public int getFoodPoints() {
		return (Integer) info.getData("food");
	}
	
	public void setFoodPoints(int value) {
		this.info.setData("food", value);
	}

	@Override
	public String getDescription() {
		return getFoodPoints() + " food points";
	}

	private static final int MAX_FOOD_LEVEL = 20;
	
	@Override
	public void give(Player player) throws RewardException {
		int food = player.getFoodLevel() + getFoodPoints() * MAX_FOOD_LEVEL / 100;
		if(food > MAX_FOOD_LEVEL) {
			food = MAX_FOOD_LEVEL;
		}
		else if(food < 0) {
			food = 0;
		}
		player.setFoodLevel(food);
	}

}
