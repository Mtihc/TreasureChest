package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class FoodRewardFactory extends RewardFactory {

	public FoodRewardFactory() {
		
	}

	@Override
	public String getLabel() {
		return "food";
	}

	@Override
	public String getGeneralDescription() {
		return "some amount of food points";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new FoodReward(info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		int food;
		try {
			food = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the amount of food points.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of food points, instead of text.", e));
			return;
		}
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected only the amount of food points."));
			return;
		}
		
		callback.onCreate(sender, args, new FoodReward(food));
	}

	@Override
	public String args() {
		return "<food points>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the amount of food points (1-100)"
		};
	}

}
