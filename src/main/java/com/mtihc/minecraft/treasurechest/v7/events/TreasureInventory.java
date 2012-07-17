package com.mtihc.minecraft.treasurechest.v7.events;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TreasureInventory implements Runnable {

	private JavaPlugin plugin;
    private long delay;
    private int taskId;

    private Inventory inventory;

    public TreasureInventory(JavaPlugin plugin, long delay, Inventory inventory) {
        this.plugin = plugin;
        this.delay = delay;
        this.taskId = 0;
        this.inventory = inventory;
    }

    /**
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    public void schedule() {
        cancel();
        taskId  = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
    }

    public void cancel() {
        if (taskId != 0) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = 0;
        }
    }

    @Override
    public void run() {
        cancel();
        execute();
    }

    protected abstract void execute();

}
