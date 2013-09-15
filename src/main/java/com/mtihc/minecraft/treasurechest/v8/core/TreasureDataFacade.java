package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class TreasureDataFacade implements ITreasureDataFacade {

	private ITreasureManagerConfiguration config;
	private ITreasureChestRepository chests;
	private ITreasureChestGroupRepository groups;
	private ITreasureChestMemory memory;

	public TreasureDataFacade(ITreasureManagerConfiguration config, ITreasureChestRepository chests, ITreasureChestGroupRepository groups, ITreasureChestMemory memory) {
		this.config = config;
		this.chests = chests;
		this.groups = groups;
		this.memory = memory;
	}

	@Override
	public Collection<Location> getAllPlayerFound(OfflinePlayer player,
			World world) {
		return memory.getAllPlayerFound(player, world);
	}

	@Override
	public long whenHasPlayerFound(OfflinePlayer player, Location location) {
		return memory.whenHasPlayerFound(player, location);
	}

	@Override
	public boolean hasPlayerFound(OfflinePlayer player, Location location) {
		return memory.hasPlayerFound(player, location);
	}

	@Override
	public void rememberPlayerFound(OfflinePlayer player, Location location) {
		memory.rememberPlayerFound(player, location);
	}

	@Override
	public void forgetPlayerFound(OfflinePlayer player, Location location) {
		memory.forgetPlayerFound(player, location);
	}

	@Override
	public void forgetPlayerFoundAll(OfflinePlayer player, World world) {
		memory.forgetPlayerFoundAll(player, world);
	}

	@Override
	public void forgetChest(Location location) {
		memory.forgetChest(location);
	}

	@Override
	public ITreasureChest getTreasure(Location location) {
		return chests.getTreasure(location);
	}

	@Override
	public void setTreasure(ITreasureChest value) {
		chests.setTreasure(value);
	}

	@Override
	public boolean hasTreasure(Location location) {
		return chests.hasTreasure(location);
	}

	@Override
	public boolean removeTreasure(Location location) {
		return chests.removeTreasure(location);
	}

	@Override
	public Set<Location> getTreasureLocations(String worldName) {
		return chests.getTreasureLocations(worldName);
	}

	@Override
	public ITreasureChestGroup getGroup(String name) {
		return groups.getGroup(name);
	}

	@Override
	public void setGroup(String name, ITreasureChestGroup value) {
		groups.setGroup(name, value);
	}

	@Override
	public boolean hasGroup(String name) {
		return groups.hasGroup(name);
	}

	@Override
	public boolean removeGroup(String name) {
		return groups.removeGroup(name);
	}

	@Override
	public Set<String> getGroupNames() {
		return groups.getGroupNames();
	}

	@Override
	public ITreasureManagerConfiguration getConfig() {
		return config;
	}

}
