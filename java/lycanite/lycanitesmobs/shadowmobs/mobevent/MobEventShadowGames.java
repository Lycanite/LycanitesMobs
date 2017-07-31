package lycanite.lycanitesmobs.shadowmobs.mobevent;

import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventShadowGames extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventShadowGames(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
    	long currentTime = world.provider.getWorldTime();
    	
    	int dayTime = 24000;
    	int targetTime = 20000;
    	
    	long excessTime = currentTime % dayTime;
    	int addedTime = dayTime;
    	if(excessTime > targetTime) {
    		targetTime += dayTime;
    	}

		if(canAffectTime)
			world.provider.setWorldTime(currentTime - excessTime + targetTime);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
