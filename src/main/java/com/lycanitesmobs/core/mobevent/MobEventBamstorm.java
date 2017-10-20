package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBamstorm extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBamstorm(String name, GroupInfo group) {
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
