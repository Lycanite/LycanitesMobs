package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
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
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase creature = (EntityCreatureBase)entity;
            if (creature.mobInfo.getRegistryName().equals("jabberwock"))
                entity.setCustomNameTag("Gooderness");
        }
	}
}
