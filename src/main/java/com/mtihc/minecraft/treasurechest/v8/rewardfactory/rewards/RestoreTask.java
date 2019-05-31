package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import com.sk89q.worldedit.world.snapshot.SnapshotRestore;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.MissingWorldException;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

abstract class RestoreTask implements Runnable {
	
	//private RestoreRepository repo;
	private final JavaPlugin plugin;
	private long delay;
	private final int subregionSize;
	private int taskId;
	private ChunkStore chunkStore;
	private final String snapshotName;
	private final String worldName;
	private final org.bukkit.util.Vector min;
	private final org.bukkit.util.Vector max;
	private RegionIterator iterator;

	RestoreTask(JavaPlugin plugin, String snapshotName, String worldName, org.bukkit.util.Vector min, org.bukkit.util.Vector max, long subregionTicks, int subregionSize) {
		this.plugin = plugin;
		this.snapshotName = snapshotName;
		this.worldName = worldName;
		this.min = min;
		this.max = max;
		this.delay = Math.max(delay, 2);
		this.subregionSize = Math.max(subregionSize, 10);
		this.taskId = -1;
	}
	
	
	public abstract String getId();

	public boolean isRunning() {
		return taskId != -1;
	}
	
	public void start() {
		if(!isRunning()) {
			onStart();
			
			iterator = new RegionIterator(
					BlockVector3.at(min.getX(), min.getY(), min.getZ()), 
					BlockVector3.at(max.getX(), max.getY(), max.getZ()), subregionSize);
			
			// toggle isRunning
			taskId = 0;
			
			
			Snapshot snapshot;
			if(snapshotName != null) {
				try {
					snapshot = WorldEdit.getInstance().getConfiguration().snapshotRepo.getSnapshot(snapshotName);
				} catch (InvalidSnapshotException e) {
					snapshot = null;
				}
			}
			else {
				try {
					snapshot = WorldEdit.getInstance().getConfiguration().snapshotRepo.getDefaultSnapshot(worldName);
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
				region.setWorld(BukkitAdapter.adapt(Bukkit.getWorld(worldName)));
			}
			restoreRegionInstantly(chunkStore, region);
		}
		else {
			if (isRunning()) {
				stop();
			}
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
	public static void restoreRegionInstantly(ChunkStore chunkStore, Region region) {		
		try {
			EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
			SnapshotRestore restore = new SnapshotRestore(chunkStore, session, region);
			restore.restore();
		} catch (NullPointerException e) {
			
		} catch (MaxChangedBlocksException e) {
			
		}
	}
	
}