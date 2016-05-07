package lycanite.lycanitesmobs.api.spawning;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SpawnTypeWater extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeWater(String typeName) {
        super(typeName);
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses te block lists.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
    	List<int[]> blockCoords = new ArrayList<int[]>();
        int range = this.getRange(world);
        BlockPos originPos = new BlockPos(x, y, z);

        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos chunkCoords = this.getRandomWaterCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		blockCoords.add(new int[] {chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ()});
        	}
        }
        
        return blockCoords;
    }
}
