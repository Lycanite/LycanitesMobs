package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;


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
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     * @return A list of integer arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
    	List<int[]> blockCoords = null;
        int range = this.getRange(world);
        ChunkPosition originPos = new ChunkPosition(x, y, z);
        
        for(int i = 0; i < this.blockLimit; i++) {
        	ChunkPosition chunkCoords = this.getRandomSkyCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<int[]>();
        		blockCoords.add(new int[] {chunkCoords.chunkPosX, chunkCoords.chunkPosY, chunkCoords.chunkPosZ});
        	}
        }
        
        return blockCoords;
    }
}
