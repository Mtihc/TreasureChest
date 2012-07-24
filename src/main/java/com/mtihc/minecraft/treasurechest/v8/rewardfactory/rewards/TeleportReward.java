package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class TeleportReward implements IReward {

	private TeleportRewardFactory factory;
	private RewardInfo info;
	
	TeleportReward(TeleportRewardFactory factory, RewardInfo info) {
		this.factory = factory;
		this.info = info;
	}

	public TeleportReward(TeleportRewardFactory factory, World world, Vector min, Vector max, int delay) {
		this.factory = factory;
		this.info = new RewardInfo("tp", new HashMap<String, Object>());
		setRegion(world, min, max);
		setDelay(delay);
	}
	

	public World getWorld() {
		return Bukkit.getWorld((String) info.getData("world"));
	}
	
	public Vector getMax() {
		return (Vector) info.getData("max");
	}
	
	public Vector getMin() {
		return (Vector) info.getData("min");
	}
	
	public void setRegion(World world, Vector min, Vector max) {
		info.setData("world", world.getName());
		info.setData("min", min);
		info.setData("max", max);
	}
	
	public int getDelay() {
		return (Integer) info.getData("delay");
	}
	
	public void setDelay(int delay) {
		info.setData("delay", delay);
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}

	@Override
	public String getDescription() {
		Vector min = getMin();
		Vector max = getMax();
		return "teleport to a random location in region (" + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + ") (" + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + ") in world \"" + getWorld().getName() + "\" after " + getDelay() + " seconds";
	}

	@Override
	public void give(Player player) throws RewardException {
		factory.teleport(player, this);
	}

}
