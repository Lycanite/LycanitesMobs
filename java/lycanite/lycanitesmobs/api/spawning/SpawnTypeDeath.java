package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.World;

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
    public boolean canSpawn(long tick, World world, int x, int y, int z, boolean rare) {
        if(world.provider.dimensionId == 0 && world.isDaytime())
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
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        return this.orderCoordsCloseToFar(coords, x, y, z);
    }
}
