package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityWendigo;
import net.minecraft.entity.EntityLiving;

public class MobEventRoasting extends MobEventYule {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventRoasting(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
        super.onSpawn(entity);
        if(entity instanceof EntityWendigo)
            entity.setCustomNameTag("Gooderness");
	}
}
