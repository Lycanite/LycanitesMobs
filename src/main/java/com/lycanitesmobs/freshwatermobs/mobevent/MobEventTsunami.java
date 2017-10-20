package com.lycanitesmobs.freshwatermobs.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
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
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
        if(MobEventManager.getInstance().canAffectWeather) {
            world.getWorldInfo().setRaining(true);
            world.getWorldInfo().setThundering(true);
        }
    }
}
