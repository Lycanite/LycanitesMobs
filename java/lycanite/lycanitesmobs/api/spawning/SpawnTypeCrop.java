package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class SpawnTypeCrop extends SpawnTypeBlockBreak {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeCrop(String typeName) {
        super(typeName);
    }


    // ==================================================
    //                     Block Harvest
    // ==================================================
    @Override
    public boolean validBlockHarvest(Block block, World world, int x, int y, int z, Entity entity) {
        return block instanceof IPlantable || block instanceof BlockVine;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z, boolean rare) {
    	double roll = world.rand.nextDouble();
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("rootriot".equalsIgnoreCase(worldExt.getMobEventType()))
    			roll /= 4;
    	}
        if(roll >= this.chance)
            return false;
        return true;
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    @Override
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        return this.orderCoordsCloseToFar(coords, x, y, z);
    }
}
