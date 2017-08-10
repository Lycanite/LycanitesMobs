package com.lycanitesmobs.core.spawning;

import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class CoordSorterNearest implements Comparator {
    public BlockPos coord;
	
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CoordSorterNearest(BlockPos coord) {
        this.coord = coord;
    }
    
    
    // ==================================================
  	//                     Compare
  	// ==================================================
	@Override
	public int compare(Object targetA, Object targetB) {
		double distanceA = this.getDistanceSqCoord((BlockPos)targetA);
        double distanceB = this.getDistanceSqCoord((BlockPos)targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
	}
	
	
    // ==================================================
  	//                  Get Distance
  	// ==================================================
    public double getDistanceSqCoord(BlockPos targetCoord) {
        return this.coord.distanceSq(targetCoord.getX(), targetCoord.getY(), targetCoord.getZ());
    }
}
