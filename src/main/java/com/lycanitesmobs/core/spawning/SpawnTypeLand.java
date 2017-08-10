package com.lycanitesmobs.core.spawning;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SpawnTypeLand extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeLand(String typeName) {
        super(typeName);
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses te block lists.
     * @param world The world to spawn in.
     * @param pos Block position.
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<BlockPos> getSpawnCoordinates(World world, BlockPos pos) {
    	List<BlockPos> blockCoords = null;
        int range = this.getRange(world);

        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos chunkCoords = this.getRandomLandCoord(world, pos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<BlockPos>();
        		blockCoords.add(chunkCoords);
        	}
        }
        
        return blockCoords;
    }
}
