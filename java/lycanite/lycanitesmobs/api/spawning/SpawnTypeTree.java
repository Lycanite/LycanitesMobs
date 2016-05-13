package lycanite.lycanitesmobs.api.spawning;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

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
    public boolean validBlockBreak(Block block, World world, BlockPos pos, Entity entity) {
        return this.isTreeLeavesBlock(block, world, pos);
    }


    // ==================================================
    //                     Block Harvest
    // ==================================================
    @Override
    public boolean validBlockHarvest(Block block, World world, BlockPos pos, Entity entity) {
        if(!super.validBlockHarvest(block, world, pos, entity))
            return false;
        return this.isTreeLogBlock(block, world, pos) || this.isTreeLeavesBlock(block, world, pos);
    }


    // ==================================================
    //                    Check Blocks
    // ==================================================
    public boolean isTreeLogBlock(Block block, World world, BlockPos pos) {
        if(ObjectLists.isInOreDictionary("logWood", block)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            for(int searchX = x - 1; searchX <= x + 1; searchX++) {
                for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
                    for(int searchY = y; searchY <= Math.min(world.getHeight(), y + 32); searchY++) {
                        Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
                        if(searchBlock != block && searchBlock != null) {
                            if(ObjectLists.isInOreDictionary("treeLeaves", searchBlock))
                                return true;
                            if(!world.isAirBlock(new BlockPos(x, searchY, z)))
                                break;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isTreeLeavesBlock(Block block, World world, BlockPos pos) {
        if(ObjectLists.isInOreDictionary("treeLeaves", block)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            for(int searchX = x - 1; searchX <= x + 1; searchX++) {
                for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
                    for(int searchY = y; searchY >= Math.max(0, y - 32); searchY--) {
                        Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
                        if(searchBlock != block && searchBlock != null) {
                            if(ObjectLists.isInOreDictionary("logWood", searchBlock)) {
                                return true;
                            }
                            if(!world.isAirBlock(new BlockPos(x, searchY, z)))
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
    public boolean canSpawn(long tick, World world, BlockPos pos, boolean rare) {
    	double roll = world.rand.nextDouble();
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("rootriot".equalsIgnoreCase(worldExt.getWorldEventType()))
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
    public List<BlockPos> orderCoords(List<BlockPos> coords, BlockPos pos) {
        return this.orderCoordsCloseToFar(coords, pos);
    }
}
