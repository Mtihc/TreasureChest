package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundUnlimitedEvent;

class BankRobberListener implements Listener {

	private BankRobberRewardFactory factory;

	BankRobberListener(BankRobberRewardFactory factory) {
		this.factory = factory;
	}
	
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		factory.onPlayerJoin(event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		factory.onPlayerQuit(event);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		factory.onPlayerKick(event);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		factory.onPlayerDeath(event);
	}
	
	@EventHandler
	public void onTreasureChestFound(TreasureChestFoundEvent event) {
		factory.onTreasureChestFound(event);
	}
	
	@EventHandler
	public void onTreasureChestFoundUnlimited(TreasureChestFoundUnlimitedEvent event) {
		factory.onTreasureChestFoundUnlimited(event);
	}
}
