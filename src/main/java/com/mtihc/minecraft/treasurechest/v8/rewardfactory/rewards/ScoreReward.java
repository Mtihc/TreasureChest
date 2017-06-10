package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
		
		if(objective == null) {
			throw new IllegalArgumentException("Parameter objective can't be null.");
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("score", score);
		data.put("objective", objective.getName());
		this.info = new RewardInfo("score", data);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public Objective getObjective() {
		String name = (String) info.getData("objective");
		return Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective(name);
	}
	
	public void setObjective(Objective value) {
		this.info.setData("objective", value.getName());
	}
	
	public int getScore() {
		return (Integer) info.getData("score");
	}
	
	public void setScore(int value) {
		this.info.setData("score", value);
	}

	@Override
	public String getDescription() {
		return getScore() + " points to " + getObjective().getName();
	}

	@Override
	public void give(Player player) throws RewardException {
		int score = getScore();
		Objective objective = getObjective();
		
		int current = objective.getScore(player.getName()).getScore();
		objective.getScore(player.getName()).setScore(current + score);
	}

}
