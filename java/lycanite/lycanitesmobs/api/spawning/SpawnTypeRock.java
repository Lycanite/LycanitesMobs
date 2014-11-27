package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class SpawnTypeRock extends SpawnTypeBase {
	public int blockBreakRadius = 1;

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeRock(String typeName) {
        super(typeName);
        CustomSpawner.instance.oreBreakSpawnTypes.add(this);
        this.blockBreakRadius = ConfigBase.getConfig(LycanitesMobs.group, "spawning").getInt("Spawner Features", "Rock Spawn Block Break Radius", this.blockBreakRadius, "The block breaking radius aroud a mob spawned from the Rock Spawner.");
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
    	double roll = world.rand.nextDouble();
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("boulderdash".equalsIgnoreCase(worldExt.getMobEventType()))
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
        if(entityLiving instanceof EntityCreatureBase) {
        	((EntityCreatureBase)entityLiving).destroyArea((int)entityLiving.posX, (int)entityLiving.posY, (int)entityLiving.posZ, 4, true, this.blockBreakRadius);
        }
    }
}
