package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SpawnTypeDeath extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeDeath(String typeName) {
        super(typeName);
        CustomSpawner.instance.deathSpawnTypes.add(this);
    }


    // ==================================================
    //                   Death Spawner
    // ==================================================
    public boolean isValidKill(EntityLivingBase entity, EntityLivingBase killer) {
    	if(entity == null || killer == null)
    		return false;
    	if(entity.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
    		return false;
    	return true;
    }
    

    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos pos, boolean rare) {
        if(world.provider.getDimension() == 0 && world.isDaytime())
            return false;
    	double roll = world.rand.nextDouble();
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("shadowgames".equalsIgnoreCase(worldExt.getWorldEventType()))
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
