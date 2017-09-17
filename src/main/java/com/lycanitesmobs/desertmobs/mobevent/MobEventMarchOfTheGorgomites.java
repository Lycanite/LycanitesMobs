package com.lycanitesmobs.desertmobs.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventMarchOfTheGorgomites extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventMarchOfTheGorgomites(String name, GroupInfo group) {
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
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity, int rank) {
		super.onSpawn(entity, rank);

        if(entity instanceof EntityCreatureBase && entity.getRNG().nextDouble() >= 0.85D) {
        	((EntityCreatureBase)entity).setSizeScale(3.0D + (0.35D * (0.5D - entity.getRNG().nextDouble())));
        }
	}
}
