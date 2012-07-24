package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class TeleportRewardListener implements Listener {

	private TeleportRewardFactory factory;

	TeleportRewardListener(TeleportRewardFactory factory) {
		this.factory = factory;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		factory.onPlayerQuit(event);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		factory.onPlayerKick(event);
	}
	

}
