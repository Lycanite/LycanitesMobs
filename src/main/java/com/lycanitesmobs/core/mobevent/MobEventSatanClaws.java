package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventSatanClaws extends MobEventYule {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventSatanClaws(String name, GroupInfo group) {
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

        if(canAffectTime)
            world.provider.setWorldTime(currentTime - excessTime + targetTime);
    }


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity, int rank) {
        super.onSpawn(entity, rank);
        entity.setCustomNameTag("Satan Claws");
	}
}
