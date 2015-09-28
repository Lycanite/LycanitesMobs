package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class SpawnTypeRock extends SpawnTypeBlockBreak {
	public int blockBreakRadius = 1;
    public boolean playerOnly = false;

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
        this.playerOnly = config.getBool("Spawner Features", "Rock Spawn Player Only", this.playerOnly, "If true, this spawn type will only react to blocks broken by actual player (for example this will stop BuildCraft Quarries from spawning Geonach).");
    }


        // ==================================================
    //                     Block Harvest
    // ==================================================
    @Override
    public boolean validBlockHarvest(Block block, World world, int x, int y, int z, Entity entity) {
        if(this.playerOnly && !(entity instanceof EntityPlayer))
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
        return isOre || block == Blocks.monster_egg;
    }


    // ==================================================
    //                      Rare Block
    // ==================================================
    @Override
    public boolean isRareBlock(Block block) {
        if(block == Blocks.diamond_ore)
           return true;
        if(block == Blocks.emerald_ore)
            return true;
        return false;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z, boolean rare) {
    	double roll = world.rand.nextDouble();
        if(rare)
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
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        return this.orderCoordsCloseToFar(coords, x, y, z);
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
        if(entityLiving instanceof EntityCreatureBase && this.blockBreakRadius > -1) {
        	((EntityCreatureBase)entityLiving).destroyArea((int)entityLiving.posX, (int)entityLiving.posY, (int)entityLiving.posZ, 4, true, this.blockBreakRadius);
        }
    }
}
