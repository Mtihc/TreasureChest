package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class PotionReward implements IReward {

	private RewardInfo info;

	public PotionReward(PotionEffect effect) {
		this.info = new RewardInfo("potion", new HashMap<String, Object>());
		setEffect(effect);
	}
	
	PotionReward(RewardInfo info) {
		this.info = info;
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public String getEffectName() {
		return (String) info.getData("type");
	}
	
	public PotionEffectType getEffectType() {
		PotionEffectType type = PotionEffectType.getByName(getEffectName());
		return type; 
	}
	
	public PotionEffect createEffect() {
		PotionEffectType type = getEffectType();
		int duration = (Integer) info.getData("duration");
		int amplifier = (Integer) info.getData("amplifier");
		return type.createEffect((int) (duration / type.getDurationModifier()), amplifier);
	}
	
	public void setEffect(PotionEffect effect) {
		info.setData("type", effect.getType().getName());
		info.setData("duration", effect.getDuration());
		info.setData("amplifier", effect.getAmplifier());
	}

	@Override
	public String getDescription() {
		return "potion effect \"" + getEffectName() + "\"";
	}

	@Override
	public void give(Player player) throws RewardException {
		PotionEffect e = createEffect();
		player.addPotionEffect(e);
	}

}
