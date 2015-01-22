package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SpawnTypeTree extends SpawnTypeBlockBreak {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeTree(String typeName) {
        super(typeName);
    }


    // ==================================================
    //                     Block Break
    // ==================================================
    @Override
    public boolean validBlockBreak(Block block, World world, int x, int y, int z, Entity entity) {
        return this.isTreeLeavesBlock(block, world, x, y, z);
    }


    // ==================================================
    //                     Block Harvest
    // ==================================================
    @Override
    public boolean validBlockHarvest(Block block, World world, int x, int y, int z, Entity entity) {
        return this.isTreeLogBlock(block, world, x, y, z) || this.isTreeLeavesBlock(block, world, x, y, z);
    }


    // ==================================================
    //                    Check Blocks
    // ==================================================
    public boolean isTreeLogBlock(Block block, World world, int x, int y, int z) {
        if(ObjectLists.isInOreDictionary("logWood", block)) {
            for(int searchX = x - 1; searchX <= x + 1; searchX++) {
                for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
                    for(int searchY = y; searchY <= Math.min(world.getHeight(), y + 32); searchY++) {
                        Block searchBlock = world.getBlock(searchX, searchY, searchZ);
                        if(searchBlock != block && searchBlock != null) {
                            if(ObjectLists.isInOreDictionary("treeLeaves", searchBlock))
                                return true;
                            if(!world.isAirBlock(x, searchY, z))
                                break;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isTreeLeavesBlock(Block block, World world, int x, int y, int z) {
        if(ObjectLists.isInOreDictionary("treeLeaves", block)) {
            for(int searchX = x - 1; searchX <= x + 1; searchX++) {
                for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
                    for(int searchY = y; searchY >= Math.max(0, y - 32); searchY--) {
                        Block searchBlock = world.getBlock(searchX, searchY, searchZ);
                        if(searchBlock != block && searchBlock != null) {
                            if(ObjectLists.isInOreDictionary("logWood", searchBlock)) {
                                return true;
                            }
                            if(!world.isAirBlock(x, searchY, z))
                                break;
                        }
                    }
                }
            }
        }
        return false;
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
