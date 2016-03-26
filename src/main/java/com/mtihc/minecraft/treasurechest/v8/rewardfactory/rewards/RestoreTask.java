package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import com.sk89q.worldedit.world.snapshot.SnapshotRestore;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.MissingWorldException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

abstract class RestoreTask implements Runnable {
	
	public static LocalWorld getLocalWorld(WorldEditPlugin worldEdit, String name) {
            return BukkitUtil.getLocalWorld(Bukkit.getWorld(name));
	}
	
	public static Vector getVector(org.bukkit.util.Vector vec) {
		return new Vector(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}
	
	//private RestoreRepository repo;
	private JavaPlugin plugin;
	private CuboidRegion region;
	private long delay;
	private int subregionSize;
	private int taskId;
	private ChunkStore chunkStore;
	private String snapshotName;
	private WorldEditPlugin worldEdit;
	private String worldName;
	private RegionIterator iterator;
	private LocalWorld localWorld;

	RestoreTask(WorldEditPlugin worldEdit, JavaPlugin plugin, String snapshotName, String worldName, org.bukkit.util.Vector min, org.bukkit.util.Vector max, long subregionTicks, int subregionSize) {
		this.worldEdit = worldEdit;
		this.plugin = plugin;
		this.snapshotName = snapshotName;
		this.worldName = worldName;
		this.region = getRegion(worldName, min, max);
		this.delay = Math.max(delay, 2);
		this.subregionSize = Math.max(subregionSize, 10);
		this.taskId = -1;
	}
	
	
	public abstract String getId();

	private CuboidRegion getRegion(String worldName, org.bukkit.util.Vector min, org.bukkit.util.Vector max) {
		return new CuboidRegion(
				getLocalWorld(worldEdit, worldName), 
				getVector(min), 
				getVector(max));
	}
	
	public CuboidRegion getRegion() {
		return region;
	}
	
	public boolean isRunning() {
		return taskId != -1;
	}
	
	public void start() {
		if(!isRunning()) {
			onStart();
			
			Vector min = region.getMinimumPoint();
			Vector max = region.getMaximumPoint();
			iterator = new RegionIterator(min, max, subregionSize);
			
			localWorld = getLocalWorld(worldEdit, worldName);
			if(localWorld == null) {
				run();
				return;
			}
			// toggle isRunning
			taskId = 0;
			
			
			Snapshot snapshot;
			if(snapshotName != null) {
				try {
					snapshot = worldEdit.getLocalConfiguration().snapshotRepo.getSnapshot(snapshotName);
				} catch (InvalidSnapshotException e) {
					snapshot = null;
				}
			}
			else {
				try {
					snapshot = worldEdit.getLocalConfiguration().snapshotRepo.getDefaultSnapshot(worldName);
				} catch (MissingWorldException e) {
					snapshot = null;
				}
			}
			
			if(snapshot != null) {
				try {
					this.chunkStore = snapshot.getChunkStore();
				} catch (Exception e) {
					this.chunkStore = null;
				} 
			}
			else {
				this.chunkStore = null;
			}
			
			if(this.chunkStore != null) {
				BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, delay, delay);
				taskId = task.getTaskId();
			}
			else {
				run();
			}
			
		}
	}
	
	public void cancel() {
		if(stop()) {
			onCancel();
		}
	}
	
	private boolean stop() {
		if(isRunning()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public synchronized void run() {
		if(chunkStore != null) {
			
			CuboidRegion region = iterator.next();
			if(region == null) {
				chunkStore = null;
				return;
			}
			else {
				region.setWorld(localWorld);
			}
			restoreRegionInstantly(chunkStore, region);
		}
		else if(isRunning()) {
			stop();
			onFinish();
		}
	}
	
	protected abstract void onStart();
	
	protected abstract void onCancel();
	
	protected abstract void onFinish();
	
	/**
	 * Restore a region
	 * 
	 * @param chunkStore
	 *            The <code>ChunkStore</code>, usually retrieved from a
	 *            <code>Snapshot</code>
	 * @param region
	 *            The region to restore
	 */
	public static void restoreRegionInstantly(ChunkStore chunkStore,
			Region region) {
		          SnapshotRestore restore = new SnapshotRestore(chunkStore, new EditSession(BukkitUtil.getLocalWorld(Bukkit.getWorld(region.getWorld().getName())), -1), region);
		
		try {
                    restore.restore();
		} catch (NullPointerException e) {
			
		} catch (MaxChangedBlocksException e) {
			
		}
	}
	
}