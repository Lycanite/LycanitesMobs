package com.lycanitesmobs.core.spawning;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SpawnTypeSleep extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeSleep(String typeName) {
        super(typeName);
        CustomSpawner.instance.sleepSpawnTypes.add(this);
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos pos, boolean rare) {
        if(world.rand.nextDouble() >= this.chance)
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
