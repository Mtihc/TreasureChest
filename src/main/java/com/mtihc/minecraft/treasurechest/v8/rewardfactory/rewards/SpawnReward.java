package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class SpawnReward implements IReward {

	private RewardInfo info;

	public SpawnReward(EntityType type, int amount, World world, Vector min, Vector max) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		this.info = new RewardInfo("spawn", data);
		
		setEntityType(type);
		setAmount(amount);
		setRegion(world, min, max);
	}

	SpawnReward(RewardInfo info) {
		this.info = info;
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public EntityType getEntityType() {
		return EntityType.fromId((Integer) info.getData("type"));
	}
	
	public void setEntityType(EntityType type) {
		info.setData("type", (int) type.getTypeId());
	}

	@Override
	public String getDescription() {
		Vector min = getMin();
		Vector max = getMax();
		return "spawn " + getAmount() + " " + getEntityType().name().toLowerCase() + " at " + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + " " + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + " in " + getWorld().getName();
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
	
	public int getAmount() {
		return (Integer) info.getData("amount");
	}
	
	public void setAmount(int value) {
		info.setData("amount", value);
	}

	@Override
	public void give(Player player) throws RewardException {
		World world = getWorld();
		Vector min = getMin();
		Vector max = getMax();
		EntityType type = getEntityType();
		int amount = getAmount();
		
		Random random = new Random();
		int i = 0;
		while(i < amount) {
			
			world.spawn(getNextRandomLocation(world, min, max, random), type.getEntityClass());
			
			i++;
		}
	}
	
	private Location getNextRandomLocation(World world, Vector min, Vector max, Random random) {
		int x = random.nextInt(max.getBlockX() - min.getBlockX() + 1) - 1 + min.getBlockX();
		int y = min.getBlockY() + 1;
		int z = random.nextInt(max.getBlockZ() - min.getBlockZ() + 1) - 1 + min.getBlockZ();
		int yaw = random.nextInt(360);
		Block block = new Location(world, x, y, z).getBlock();
		Block above = block.getRelative(0, 1, 0);
		while(!block.isEmpty() && !above.isEmpty()) {
			block = above;
			above = block.getRelative(0, 1, 0);
		}
		Location loc = block.getLocation();
		loc.setYaw(yaw);
		return loc;
	}

}
