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
     * @param originPos Position to spawn around.
     * @return A list of int BlockPos, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<BlockPos> getSpawnCoordinates(World world, BlockPos originPos) {
    	List<BlockPos> blockCoords = new ArrayList<BlockPos>();
        int range = this.getRange(world);

        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos spawnPos = this.getRandomWaterCoord(world, originPos, range);
        	if(spawnPos != null) {
        		blockCoords.add(spawnPos);
        	}
        }
        
        return blockCoords;
    }
}
