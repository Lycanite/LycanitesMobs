package com.lycanitesmobs.core.spawner;

import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class CoordSorterFurthest implements Comparator {
    public BlockPos coord;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CoordSorterFurthest(BlockPos coord) {
        this.coord = coord;
    }
    
    
    // ==================================================
  	//                     Compare
  	// ==================================================
	@Override
	public int compare(Object targetA, Object targetB) {
		double distanceA = this.getDistanceSqCoord((BlockPos)targetA);
        double distanceB = this.getDistanceSqCoord((BlockPos)targetB);
        return distanceA > distanceB ? -1 : (distanceA < distanceB ? 1 : 0);
	}
	
	
    // ==================================================
  	//                  Get Distance
  	// ==================================================
    public double getDistanceSqCoord(BlockPos targetCoord) {
        return this.coord.distanceSq(targetCoord.getX(), targetCoord.getY(), targetCoord.getZ());
    }
}
