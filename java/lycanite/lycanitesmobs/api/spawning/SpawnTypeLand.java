package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;


public class SpawnTypeLand extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeLand(String typeName) {
        super(typeName);
    }


    // ==================================================
    //               Coordinate Searching
    // ==================================================
    /** ========== Search for Block Coordinates ==========
     * Returns all blocks around the xyz position in the given world as coordinates. Uses this Spawn Type's range.
     * @param world The world to search for coordinates in.
     * @param x X position to search near.
     * @param y Y position to search near.
     * @param z Z position to search near.
     * @return Returns a list for coordinates for spawning from.
     */
    @Override
    public List<int[]> searchForBlockCoords(World world, int x, int y, int z) {
    	List<int[]> blockCoords = null;
        int range = this.getRange(world);
        
        for(int i = 0; i < Math.min(this.blockLimit, this.mobLimit); i++) {
        	ChunkPosition chunkCoords = this.getRandomLandCoord(world, world.getChunkFromChunkCoords(x, z), range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<int[]>();
        		blockCoords.add(new int[] {chunkCoords.chunkPosX, chunkCoords.chunkPosY, chunkCoords.chunkPosZ});
        	}
        }
        
        return blockCoords;
    }
}
