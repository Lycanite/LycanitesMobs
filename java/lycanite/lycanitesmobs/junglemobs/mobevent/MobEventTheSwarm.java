package lycanite.lycanitesmobs.junglemobs.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.junglemobs.entity.EntityConba;
import net.minecraft.entity.EntityLiving;

public class MobEventTheSwarm extends MobEventBase {

    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventTheSwarm(String name, GroupInfo group) {
		super(name, group);
	}
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
		if(entity instanceof EntityConba) {
			((EntityConba)entity).vespidInfection = true;
		}
	}
}
