package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class HealthRewardFactory extends RewardFactory {

	public HealthRewardFactory() {
		
	}

	@Override
	public String getLabel() {
		return "health";
	}

	@Override
	public String getGeneralDescription() {
		return "some amount of health";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new HealthReward(info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		int health;
		try {
			health = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the amount of health.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of health, instead of text.", e));
			return;
		}
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected only the amount of health."));
			return;
		}
		
		callback.onCreate(sender, args, new HealthReward(health));
	}

	@Override
	public String args() {
		return "<health>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the amount of health (1-100)"
		};
	}

}
