package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;


public class DoubleBlockInventory implements IBlockInventory {

	protected IBlockInventory leftSide;
	protected IBlockInventory rightSide;

	public DoubleBlockInventory(DoubleChest doubleChest) {
		DoubleChestInventory inv = (DoubleChestInventory) doubleChest.getInventory();
		BlockState leftBlock = (BlockState) doubleChest.getLeftSide();
		BlockState rightBlock = (BlockState) doubleChest.getRightSide();
		leftSide = new BlockInventory(leftBlock.getLocation(), inv.getLeftSide());
		rightSide = new BlockInventory(rightBlock.getLocation(), inv.getRightSide());
	}
	
	public DoubleBlockInventory(Map<String, Object> values) {
		this.leftSide = (IBlockInventory) values.get("left-side");
		this.rightSide = (IBlockInventory) values.get("right-side");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("left-side", getLeftSide());
		values.put("right-side", getRightSide());
		return values;
	}

	@Override
	public Location getLocation() {
		return getLeftSide().getLocation();
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	@Override
	public int getSize() {
		return InventoryType.CHEST.getDefaultSize() * 2;
	}

	@Override
	public ItemStack[] getContents() {
		int size = InventoryType.CHEST.getDefaultSize();
		
		ItemStack[] left = getLeftSide().getContents();
		ItemStack[] right = getRightSide().getContents();
		ItemStack[] combined = new ItemStack[size * 2];
		
		for (int i = 0; i < size; i++) {
			combined[i] = left[i];
		}
		for (int i = 0; i < size; i++) {
			combined[i + size] = right[i];
		}
		
		return combined;
	}

	@Override
	public void setContents(ItemStack[] contents) {
		int size = InventoryType.CHEST.getDefaultSize();
		
		ItemStack[] left = new ItemStack[size];
		ItemStack[] right = new ItemStack[size];
		
		for (int i = 0; i < size; i++) {
			left[i] = contents[i];
		}
		for (int i = 0; i < size; i++) {
			right[i] = contents[i + size];
		}
		
		getLeftSide().setContents(left);
		getRightSide().setContents(right);
	}
	
	public IBlockInventory getLeftSide() {
		return leftSide;
	}
	
	public IBlockInventory getRightSide() {
		return rightSide;
	}

}
