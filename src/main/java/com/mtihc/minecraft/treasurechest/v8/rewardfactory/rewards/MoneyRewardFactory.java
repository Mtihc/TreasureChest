package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class MoneyRewardFactory extends RewardFactory {

	private Economy economy;

	public MoneyRewardFactory() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if(economyProvider == null) {
				economy = null;
			}
			else {
				economy = economyProvider.getProvider();
			}
        }
		
	}

	@Override
	public String getLabel() {
		return "money";
	}

	@Override
	public String getGeneralDescription() {
		return "some amount of money.";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new MoneyReward(this, info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		
		double money;
		try {
			money = Double.parseDouble(args[0]);
		} catch(IndexOutOfBoundsException | NullPointerException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected the amount of money.", e));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected the amount of money, instead of text.", e));
			return;
		}
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Too many arguments. Expected the amount of money."));
			return;
		}
		if(money <= 0) {
			callback.onCreateException(sender, args, new RewardException("Expected a number, larget than zero."));
		}
		
		callback.onCreate(sender, args, new MoneyReward(this, money));
	}

	@Override
	public String args() {
		return "<money>";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify some amount of money."
		};
	}

	String format(double money) {
		if(economy == null) {
			return String.valueOf(money);
		}
		else {
			return economy.format(money);
		}
	}

	void depositPlayer(String name, double money) throws RewardException {
		if(economy == null) {
			throw new RewardException("No money reward. The Vault plugin is probably not installed.");
		}
		else {
			economy.depositPlayer(name, money);
		}
	}

}
