package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.Calendar;

public class MobEventRudolph extends MobEventYule {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventRudolph(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
        super.onSpawn(entity);
        entity.setCustomNameTag("Rudolph");
	}
}
