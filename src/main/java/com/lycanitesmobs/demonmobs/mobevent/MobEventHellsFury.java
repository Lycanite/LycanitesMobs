package com.lycanitesmobs.demonmobs.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventHellsFury extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventHellsFury(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
        if(MobEventManager.getInstance().canAffectWeather) {
            world.getWorldInfo().setRaining(false);
            world.getWorldInfo().setThundering(false);
        }
    }
}
