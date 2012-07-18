package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class LevelRewardFactory extends RewardFactory {

	public LevelRewardFactory() {
		
	}

	@Override
	public String getLabel() {
		return "level";
	}

	@Override
	public String getGeneralDescription() {
		return "Some amount of experience levels";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new LevelReward(info);
	}

	@Override
	public void createRewardInfo(CommandSender sender, String[] args,
			CreateCallback callback) {
		int levels;
		try {
			levels = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the amount of experience levels.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of experience levels, instead of text.", e));
			return;
		}
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected only the amount of experience levels."));
			return;
		}
		
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("levels", levels);
		RewardInfo info = new RewardInfo(getLabel(), data);
		callback.onCreate(sender, args, info);
	}

	@Override
	public String args() {
		return "<levels>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the amount of levels."
		};
	}

}
