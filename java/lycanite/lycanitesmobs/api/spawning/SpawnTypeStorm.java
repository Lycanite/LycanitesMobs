package lycanite.lycanitesmobs.api.spawning;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

public class SpawnTypeStorm extends SpawnTypeSky {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeStorm(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
        CustomSpawner.instance.lightningStrikeTypes.add(this);
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
        if(this.rate == 0 || tick % this.rate != 0)
            return false;
    	if(!world.isRaining())
    		return false;
    	double chance = this.chance;
    	if(world.isThundering())
    		chance = Math.min(chance * 2, 1.0D);
        if(world.rand.nextDouble() >= chance)
            return false;
        return true;
    }
    
    
    // ==================================================
    //               Coordinate Checking
    // ==================================================
    /** Checks if th eprovided world coordinate is valid for this spawner to use. This should not include block type/material checks as they are done elsewhere.
     * @param world The world to search for coordinates in.
     * @param x X position to check.
     * @param y Y position to check.
     * @param z Z position to check.
     * @return Returns true if it is a valid coordinate so that it can be added to the list.
     */
    public boolean isValidCoord(World world, int x, int y, int z) {
    	if(!world.canLightningStrikeAt(x, y, z))
    		return false;
    	return super.isValidCoord(world, x, y, z);
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
    	if(entityLiving.getRNG().nextFloat() >= 0.25F) {
	    	EntityLightningBolt lightning = new EntityLightningBolt(world, entityLiving.posX, entityLiving.posY, entityLiving.posZ);
	    	world.spawnEntityInWorld(lightning);
    	}
        super.spawnEntity(world, entityLiving);
    }
}
