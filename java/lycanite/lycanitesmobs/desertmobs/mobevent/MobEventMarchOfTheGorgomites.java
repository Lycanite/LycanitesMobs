package lycanite.lycanitesmobs.desertmobs.mobevent;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventMarchOfTheGorgomites extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventMarchOfTheGorgomites(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
        if(canAffectWeather) {
            world.getWorldInfo().setRaining(false);
            world.getWorldInfo().setThundering(false);
        }
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);

        if(entity instanceof EntityCreatureBase && entity.getRNG().nextDouble() >= 0.85D) {
        	((EntityCreatureBase)entity).setSizeScale(3.0D + (0.35D * (0.5D - entity.getRNG().nextDouble())));
        }
	}
}
