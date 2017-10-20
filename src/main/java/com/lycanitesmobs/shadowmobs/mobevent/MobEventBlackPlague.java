package com.lycanitesmobs.shadowmobs.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBlackPlague extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBlackPlague(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
	public void onStart(World world, int rank) {
		super.onStart(world, rank);
    	long currentTime = world.provider.getWorldTime();
    	
    	int dayTime = 24000;
    	int targetTime = 20000;
    	
    	long excessTime = currentTime % dayTime;
    	int addedTime = dayTime;
    	if(excessTime > targetTime) {
    		targetTime += dayTime;
    	}

		if(MobEventManager.getInstance().canAffectTime)
			world.provider.setWorldTime(currentTime - excessTime + targetTime);
    }
}
