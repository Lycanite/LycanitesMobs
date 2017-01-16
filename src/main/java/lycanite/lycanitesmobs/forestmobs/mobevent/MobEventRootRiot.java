package lycanite.lycanitesmobs.forestmobs.mobevent;

import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;

public class MobEventRootRiot extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventRootRiot(String name, GroupInfo group) {
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
