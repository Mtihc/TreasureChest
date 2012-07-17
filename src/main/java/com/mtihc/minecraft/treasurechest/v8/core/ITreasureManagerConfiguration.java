package com.mtihc.minecraft.treasurechest.v8.core;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest.Message;

public interface ITreasureManagerConfiguration {

	public String getDefaultMessage(Message messageId);

	public boolean getDefaultIgnoreProtection();

}
