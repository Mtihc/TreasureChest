package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class ScoreRewardFactory extends RewardFactory {

	public ScoreRewardFactory() {
		
	}

	@Override
	public String getLabel() {
		return "score";
	}

	@Override
	public String getGeneralDescription() {
		return "some amount of score in a scoreboard objective";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new HealthReward(info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		Objective objective;
		int score;
		try {
			objective = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective(args[0]);
			if (objective == null) {
				callback.onCreateException(sender, args, new RewardException("Objective does not exist.", new Exception("Objective does not exist.")));
			}
			score = Integer.parseInt(args[1]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the name of objective and points to give.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of points, instead of text.", e));
			return;
		}
		if(args.length > 2) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected only name of objective and the amount of points to give."));
			return;
		}
		
		callback.onCreate(sender, args, new ScoreReward(objective, score));
	}

	@Override
	public String args() {
		return "<Objective> <Score>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the objective to give points to and the amount of points (~-2000000 - ~2000000)"
		};
	}

}
