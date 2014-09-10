package lycanite.lycanitesmobs.freshwatermobs.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventTsunami extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventTsunami(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart(World world) {
        super.onStart(world);
		world.getWorldInfo().setRaining(true);
		world.getWorldInfo().setThundering(true);
    }


    // ==================================================
    //                      Finish
    // ==================================================
    public void onFinish() {
        super.onFinish();
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
