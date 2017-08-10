package com.lycanitesmobs.core.spawning;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SpawnTypeSky extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeSky(String typeName) {
        super(typeName);
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses the block lists.
     * @param world The world to spawn in.
     * @param originPos Block position to spawn around.
     * @return A list of integer arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<BlockPos> getSpawnCoordinates(World world, BlockPos originPos) {
    	List<BlockPos> blockCoords = null;
        int range = this.getRange(world);
        
        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos chunkCoords = this.getRandomSkyCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<BlockPos>();
        		blockCoords.add(chunkCoords);
        	}
        }
        
        return blockCoords;
    }
}
