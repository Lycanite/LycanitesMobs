package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLiving;
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
        if(entityLiving instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entityLiving;
            if(!entityCreature.isStrongSwimmer() && entityCreature.canDive()) {
                BlockPos modifiedSpawnPos = entityCreature.getPosition();
                for (modifiedSpawnPos = modifiedSpawnPos.down(); modifiedSpawnPos.getY() > 0 && !world.getBlockState(modifiedSpawnPos).getMaterial().isSolid(); modifiedSpawnPos = modifiedSpawnPos.down()) {}
                modifiedSpawnPos = modifiedSpawnPos.up();
                entityCreature.setLocationAndAngles(modifiedSpawnPos.getX(), modifiedSpawnPos.getY(), modifiedSpawnPos.getZ(), entityCreature.rotationYaw, entityCreature.rotationPitch);
            }
        }
        super.spawnEntity(world, entityLiving, rank);
    }
}
