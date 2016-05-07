package lycanite.lycanitesmobs.api.spawning;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SpawnTypeLunar extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeLunar(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z, boolean rare) {
        if(!super.canSpawn(tick, world, x, y, z, rare))
        	return false;
    	if(world.provider.getMoonPhase(world.getWorldTime()) != 0 || world.provider.isDaytime())
    		return false;
        return true;
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
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
    	List<int[]> blockCoords = null;
        int range = this.getRange(world);
        BlockPos originPos = new BlockPos(x, y, z);
        
        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos chunkCoords = this.getRandomSkyCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<int[]>();
        		blockCoords.add(new int[] {chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ()});
        	}
        }
        
        return blockCoords;
    }
}
