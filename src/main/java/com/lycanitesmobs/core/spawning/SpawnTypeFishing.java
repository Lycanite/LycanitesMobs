package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SpawnTypeFishing extends SpawnTypeBase {
    /** The current hook entity to copy velocities from when spawning. **/
    public Entity hookEntity;

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeFishing(String typeName) {
        super(typeName);
        CustomSpawner.instance.fishingTypes.add(this);
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    @Override
    public List<BlockPos> orderCoords(List<BlockPos> coords, BlockPos pos) {
        return this.orderCoordsCloseToFar(coords, pos);
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
    public void spawnEntity(World world, EntityLiving entityLiving) {
        super.spawnEntity(world, entityLiving);
        if(this.hookEntity != null) {
            entityLiving.setVelocity(this.hookEntity.motionX, this.hookEntity.motionY, this.hookEntity.motionZ);
        }
    }


    // ==================================================
    //                 Set Hook Entity
    // ==================================================
    public void setHookEntity(Entity hookEntity) {
        this.hookEntity = hookEntity;
    }
}
