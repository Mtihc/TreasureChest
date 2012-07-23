package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class ExplosionReward implements IReward {
	
	private RewardInfo info;
	
	public ExplosionReward(Location location) {
		this(location, 4);
	}
	
	public ExplosionReward(Location location, int power) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("power", power);
		data.put("world", location.getWorld().getName());
		data.put("coords", location.toVector());
		this.info = new RewardInfo("explosion", data);
	}

	ExplosionReward(RewardInfo info) {
		this.info = info;
	}

	@Override
	public String getDescription() {
		Location loc = getLocation();
		return "explosion with power of " + getPower() + " at " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + " in " + loc.getWorld().getName();
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}

	@Override
	public void give(Player player) throws RewardException {
		player.getWorld().createExplosion(getLocation(), getPower());
	}
	
	public int getPower() {
		return (Integer) info.getData("power");
	}
	
	public void setPower(int value) {
		info.setData("power", value);
	}

	public Location getLocation() {
		World world = Bukkit.getWorld((String) info.getData("world"));
		Vector coords = (Vector) info.getData("coords");
		return coords.toLocation(world);
	}
	
	public void setLocation(Location value) {
		info.setData("world", value.getWorld().getName());
		info.setData("coords", value.toVector());
	}
}
