package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.List;

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
    public boolean validBlockHarvest(Block block, World world, BlockPos pos, Entity entity) {
        if(!super.validBlockHarvest(block, world, pos, entity))
            return false;
        return block instanceof IPlantable || block instanceof BlockVine;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos originPos, int rank) {
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
