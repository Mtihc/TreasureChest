package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import org.bukkit.command.CommandSender;


public abstract class RewardFactory {

	public RewardFactory() {
		
	}

	public abstract String getLabel();
	
	public abstract String getGeneralDescription();
	
	public abstract IReward createReward(
			RewardInfo info) throws RewardException;
	
	public abstract void createRewardInfo(
			CommandSender sender, 
			String[] args, 
			CreateCallback callback);


	public interface CreateCallback {
		void onCreate(CommandSender sender, String[] args, RewardInfo info);
		void onCreateException(CommandSender sender, String[] args, RewardException e);
	}


	public abstract String args();
	
	public abstract String[] help();

	
}
