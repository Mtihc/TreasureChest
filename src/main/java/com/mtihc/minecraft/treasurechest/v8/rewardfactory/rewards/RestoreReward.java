package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class RestoreReward implements IReward {
	
	private RestoreRewardFactory factory;
	private RewardInfo info;

	RestoreReward(RestoreRewardFactory factory, RewardInfo info) {
		this.factory = factory;
		this.info = info;
	}
	
	public RestoreReward(RestoreRewardFactory factory, String snapshotName, String worldName, Vector min, Vector max) {
		this.factory = factory;
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("world", worldName);
		data.put("min", min);
		data.put("max", max);
		data.put("snapshot", snapshotName);
		this.info = new RewardInfo("restore", data);
	}


	@Override
	public String getDescription() {
		Vector min = getMin();
		Vector max = getMax();
		return "restore " + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + " " + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + " in " + getWorldName() + " using snapshot \"" + getSnapshotName() + "\"";
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}

	@Override
	public void give(Player player) throws RewardException {
		factory.restore(player, this);
	}

	/**
	 * @return the worldName
	 */
	public String getWorldName() {
		return (String) info.getData("world");
	}

	/**
	 * @param worldName the worldName to set
	 */
	public void setWorldName(String worldName) {
		info.setData("world", worldName);
	}

	/**
	 * @return the min
	 */
	public Vector getMin() {
		return (Vector) info.getData("min");
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Vector min) {
		info.setData("min", min);
	}

	/**
	 * @return the max
	 */
	public Vector getMax() {
		return (Vector) info.getData("max");
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Vector max) {
		info.setData("max", max);
	}

	public String getSnapshotName() {
		return (String) info.getData("snapshot");
	}
	
	public void setSnapshotName(String snapshot) {
		info.setData("snapshot", snapshot);
	}

}
