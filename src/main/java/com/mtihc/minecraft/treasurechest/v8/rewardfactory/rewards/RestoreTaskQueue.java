package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RestoreTaskQueue {

	public interface Observer {
		void onRestoreAddToQueue(RestoreTask restore);
		void onRestoreStart(RestoreTask restore);
		void onRestoreFinish(RestoreTask restore);
		void onRestoreCancel(RestoreTask restore);
	}
	
	private LinkedHashMap<String, RestoreTask> queue = new LinkedHashMap<String, RestoreTask>();
	private RestoreTask currentTask;
	private ArrayBlockingQueue<RestoreTask> q = new ArrayBlockingQueue<RestoreTask>(10, true);
	
	private Set<Observer> observers = new LinkedHashSet<Observer>();
	
	public RestoreTaskQueue() {
		
	}
	
	public RestoreTaskQueue(Observer observer) {
		this();
		addObserver(observer);
	}
	
	
	public boolean addObserver(Observer o) {
		return observers.add(o);
	}
	
	public boolean hasObserver(Observer o) {
		return observers.contains(o);
	}
	
	public boolean removeObserver(Observer o) {
		return observers.remove(o);
	}

	
	
	
	public void add(final String id, JavaPlugin plugin, String snapshotName, String worldName, Vector min, Vector max, long subregionTicks, int subregionSize) {
		
		RestoreTask restore = new RestoreTask(plugin, snapshotName, worldName, min, max, subregionTicks, subregionSize) {

			@Override
			public String getId() {
				return id;
			}
			
			@Override
			protected void onStart() {
				onRestoreStart(this);
			}
			
			@Override
			protected void onFinish() {
				onRestoreFinish(this);
			}
			
			@Override
			protected void onCancel() {
				onRestoreCancel(this);
			}
		};
		
		if(q.isEmpty() && currentTask == null) {
			next(restore);
		}
		else {
			if(q.offer(restore)) {
				onRestoreAddToQueue(restore);
			}
			
		}
	}
	
	public RestoreTask[] getQueue() {
		return q.toArray(new RestoreTask[q.size()]);
	}
	
	public void remove(String id) {
		if(currentTask != null && currentTask.getId().equals(id)) {
			currentTask.cancel();
		}
		else {
			Iterator<RestoreTask> iterator = q.iterator();
			while(iterator.hasNext()) {
				RestoreTask r = iterator.next();
				if(r.getId().equals(id)) {
					iterator.remove();
					onRestoreCancel(queue.remove(id));
					break;
				}
			}
			
		}
	}
	
	public boolean has(String id) {
		if(currentTask != null && currentTask.getId().equals(id) && currentTask.isRunning()) {
			return true;
		}
		Iterator<RestoreTask> iterator = q.iterator();
		while(iterator.hasNext()) {
			RestoreTask r = iterator.next();
			if(r.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	private void next() {
		
		RestoreTask r = q.poll();
		if(r != null) {
			next(r);
		}
		else if(!q.isEmpty()) {
			next();
		}
	}
	
	private void next(RestoreTask restore) {
		restore.start();
	}
	
	
	private void onRestoreCancel(RestoreTask restore) {
		
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreCancel(restore);
		}
		
		if(RestoreTaskQueue.this.currentTask == restore) {
			RestoreTaskQueue.this.currentTask = null;
			next();
		}
		
	}
	
	private void onRestoreFinish(RestoreTask restore) {
		
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreFinish(restore);
		}
		
		if(RestoreTaskQueue.this.currentTask == restore) {
			RestoreTaskQueue.this.currentTask = null;
			next();
		}
		
	}
	
	private void onRestoreAddToQueue(RestoreTask restore) {
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreAddToQueue(restore);
		}
	}
	
	private void onRestoreStart(RestoreTask restore) {
		RestoreTaskQueue.this.currentTask = restore;
		
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreStart(restore);
		}
	}
	
	
	
	
}
