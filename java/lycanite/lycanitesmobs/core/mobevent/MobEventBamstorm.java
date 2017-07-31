package lycanite.lycanitesmobs.core.mobevent;

import lycanite.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBamstorm extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBamstorm(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
        if(canAffectWeather) {
            world.getWorldInfo().setRaining(true);
            world.getWorldInfo().setThundering(true);
        }
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
