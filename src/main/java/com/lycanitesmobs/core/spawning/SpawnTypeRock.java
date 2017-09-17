package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SpawnTypeRock extends SpawnTypeBlockBreak {
	public int blockBreakRadius = 1;

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeRock(String typeName) {
        super(typeName);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");

        this.blockBreakRadius = config.getInt("Spawner Features", "Rock Spawn Block Break Radius", this.blockBreakRadius, "The block breaking radius aroud a mob spawned from the Rock Spawner.");
    }


        // ==================================================
    //                     Block Harvest
    // ==================================================
    @Override
    public boolean validBlockHarvest(Block block, World world, BlockPos pos, Entity entity) {
        if(!super.validBlockHarvest(block, world, pos, entity))
            return false;
        String blockName = block.getUnlocalizedName();
        String[] blockNameParts = blockName.split("\\.");
        boolean isOre = false;
        for(String blockNamePart : blockNameParts) {
            int blockNamePartLength = blockNamePart.length();
            if(blockNamePartLength >= 3) {
                if(blockNamePart.substring(0, 3).equalsIgnoreCase("ore") || blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("ore")) {
                    isOre = true;
                    break;
                }
            }
        }
        return isOre || block == Blocks.MONSTER_EGG;
    }


    // ==================================================
    //                      Rare Block
    // ==================================================
    @Override
    public boolean isRareBlock(Block block) {
        if(block == Blocks.DIAMOND_ORE)
           return true;
        if(block == Blocks.EMERALD_ORE)
            return true;
        return false;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos pos, int rank) {
    	double roll = world.rand.nextDouble();
        if(rank > 0)
            roll /= 4;
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("boulderdash".equalsIgnoreCase(worldExt.getWorldEventType()))
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
        if(entityLiving instanceof EntityCreatureBase && this.blockBreakRadius > -1) {
        	((EntityCreatureBase)entityLiving).destroyArea((int)entityLiving.posX, (int)entityLiving.posY, (int)entityLiving.posZ, 4, true, this.blockBreakRadius);
        }
    }
}
