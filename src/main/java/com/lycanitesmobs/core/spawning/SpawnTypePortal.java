package com.lycanitesmobs.core.spawning;


import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class SpawnTypePortal extends SpawnTypeBlock {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypePortal(String typeName) {
        super(typeName);
    }


    // ==================================================
    //                  Spawn Entity
    // ==================================================
    /**
     * Spawn an entity in the provided world. The mob should have already been positioned.
     * @param world The world to spawn in.
     * @param entityLiving The entity to spawn.
     */
    @Override
    public void spawnEntity(World world, EntityLiving entityLiving, int rank) {
        super.spawnEntity(world, entityLiving, rank);
        entityLiving.timeUntilPortal = Integer.MAX_VALUE;
    }
}
