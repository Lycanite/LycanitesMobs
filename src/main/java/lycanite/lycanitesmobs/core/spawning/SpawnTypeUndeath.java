package lycanite.lycanitesmobs.core.spawning;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupDemon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnTypeUndeath extends SpawnTypeDeath {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeUndeath(String typeName) {
        super(typeName);
        CustomSpawner.instance.deathSpawnTypes.add(this);
    }


    // ==================================================
    //                   Death Spawner
    // ==================================================
    public boolean isValidKill(EntityLivingBase entity, EntityLivingBase killer) {
    	if(entity == null || killer == null)
    		return false;
    	if(entity.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD || entity instanceof IGroupDemon || entity.getClass() == ObjectManager.getMob("geist"))
    		return false;
    	return true;
    }
    

    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos pos, boolean rare) {
    	double roll = world.rand.nextDouble();
    	ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
    	if(worldExt != null) {
    		if("blackplague".equalsIgnoreCase(worldExt.getWorldEventType()))
    			roll /= 4;
    	}
        if(roll >= this.chance)
            return false;
        return true;
    }
}
