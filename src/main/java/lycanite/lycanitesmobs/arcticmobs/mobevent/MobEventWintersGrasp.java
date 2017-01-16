package lycanite.lycanitesmobs.arcticmobs.mobevent;

import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;

public class MobEventWintersGrasp extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventWintersGrasp(String name, GroupInfo group) {
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
