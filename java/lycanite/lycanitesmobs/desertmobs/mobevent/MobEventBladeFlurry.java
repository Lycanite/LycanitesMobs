package lycanite.lycanitesmobs.desertmobs.mobevent;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBladeFlurry extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBladeFlurry(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setThundering(false);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);

        double roll = entity.getRNG().nextDouble();
        if(entity instanceof EntityCreatureBase) {
            if(roll >= 0.8D && roll < 0.9D)
        	    ((EntityCreatureBase)entity).setSizeScale(3.0D + (0.35D * (0.5D - entity.getRNG().nextDouble())));
            else if(roll >= 0.9D)
                ((EntityCreatureBase)entity).setSizeScale(0.5D - (0.2D * (0.5D - entity.getRNG().nextDouble())));
        }
	}
}
