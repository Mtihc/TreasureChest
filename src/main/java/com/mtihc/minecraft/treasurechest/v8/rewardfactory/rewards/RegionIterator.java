package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;


public class RegionIterator {

	private int x;
	private int y;
	private int z;
	
	private int size;
	private BlockVector3 min;
	private BlockVector3 max;

	public RegionIterator(BlockVector3 min, BlockVector3 max, int subregionSize) {
		this.size = subregionSize;
		this.min = min;
		this.max = max;
		
		reset();
	}
	
	public void reset() {

		x = min.getBlockX();
		y = min.getBlockY();
		z = min.getBlockZ();
		
	}

	public CuboidRegion next() {
		
		if(y > max.getBlockY()) {
			reset();
			return null;
		}
		

		CuboidRegion result = createRegion();
		
		if(x + size > max.getBlockX()) {
			x = min.getBlockX();
			if(z + size > max.getBlockZ()) {
				z = min.getBlockZ();
				if(y + size > max.getBlockY()) {
					// will stop next time
					y+=size;
					return result;
				}
				else {
					y+=size;
				}
			}
			else {
				z+=size;
			}
		}
		else {
			x+=size;
		}
		return result;
	}

	private CuboidRegion createRegion() {
		
		int newX = x;
		int newY = y;
		int newZ = z;
		
		BlockVector3 minimum = BlockVector3.at(newX, newY, newZ);
		
		newX += size;
		newY += size;
		newZ += size;
		
		if(newX > max.getBlockX()) {
			newX = max.getBlockX();
		}
		if(newY > max.getBlockY()) {
			newY = max.getBlockY();
		}
		if(newZ > max.getBlockZ()) {
			newZ = max.getBlockZ();
		}
		
		BlockVector3 maximum = BlockVector3.at(newX, newY, newZ);
		
		return new CuboidRegion(minimum, maximum);
	}
}
