package lycanite.lycanitesmobs.plainsmobs.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;

public class MobEventWindStorm extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventWindStorm(String name, GroupInfo group) {
		super(name, group);
	}
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
