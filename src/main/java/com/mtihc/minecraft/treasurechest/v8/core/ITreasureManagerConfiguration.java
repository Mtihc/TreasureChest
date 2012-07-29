package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.List;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest.Message;

public interface ITreasureManagerConfiguration {

	public String getDefaultMessage(Message messageId);

	public boolean getDefaultIgnoreProtection();

	int getSubregionSize();

	int getSubregionTicks();

	List<String> getRanks();

}
