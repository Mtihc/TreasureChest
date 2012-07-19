package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

class FlyRewardFactoryListener implements Listener {

	private FlyRewardFactory factory;

	FlyRewardFactoryListener(FlyRewardFactory factory) {
		this.factory = factory;
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		factory.cancelAllFlight();
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		factory.cancelFlight(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKicked(PlayerKickEvent event) {
		factory.cancelFlight(event.getPlayer());
	}
}
