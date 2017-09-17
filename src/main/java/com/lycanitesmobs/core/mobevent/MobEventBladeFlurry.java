package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBladeFlurry extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBladeFlurry(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setThundering(false);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
    public void onSpawn(EntityLiving entity, int rank) {
        super.onSpawn(entity, rank);

        double roll = entity.getRNG().nextDouble();
        if(entity instanceof EntityCreatureBase) {
            if(roll >= 0.8D && roll < 0.9D)
        	    ((EntityCreatureBase)entity).setSizeScale(3.0D + (0.35D * (0.5D - entity.getRNG().nextDouble())));
            else if(roll >= 0.9D)
                ((EntityCreatureBase)entity).setSizeScale(0.5D - (0.2D * (0.5D - entity.getRNG().nextDouble())));
        }
	}
}
