package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class AirRewardFactory extends RewardFactory {

	public AirRewardFactory() {
		
	}

	@Override
	public String getLabel() {
		return "air";
	}

	@Override
	public String getGeneralDescription() {
		return "some amount of air";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new AirReward(info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		int air;
		try {
			air = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the amount of air.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of air, instead of text.", e));
			return;
		}
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected only the amount of air."));
			return;
		}
		
		callback.onCreate(sender, args, new AirReward(air));
	}

	@Override
	public String args() {
		return "<air>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the amount of air (1-100)"
		};
	}

}
