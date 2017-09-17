package com.lycanitesmobs.mountainmobs.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBoulderDash extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBoulderDash(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
        if(canAffectWeather) {
            world.getWorldInfo().setRaining(false);
            world.getWorldInfo().setThundering(false);
        }
    }
}
