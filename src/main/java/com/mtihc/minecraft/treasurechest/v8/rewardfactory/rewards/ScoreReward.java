package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class ScoreReward implements IReward {

	private RewardInfo info;

	protected ScoreReward(RewardInfo info) {
		this.info = info;
	}
	
	public ScoreReward(Objective objective, int score) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("score", score);
		data.put("objective", objective);
		this.info = new RewardInfo("score", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public Objective getObjective() {
		return (Objective) info.getData("objective");
	}
	
	public void setObjective(Objective value) {
		this.info.setData("objective", value);
	}
	
	public int getScore() {
		return (Integer) info.getData("score");
	}
	
	public void setScore(int value) {
		this.info.setData("score", value);
	}

	@Override
	public String getDescription() {
		return getScore() + " points to " + getObjective().toString();
	}

	@Override
	public void give(Player player) throws RewardException {
		int score = getScore();
		Objective objective = getObjective();
		
		int current = objective.getScore(player).getScore();
		objective.getScore(player).setScore(current + score);
	}

}
