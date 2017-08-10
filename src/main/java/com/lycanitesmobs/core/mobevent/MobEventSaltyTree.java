package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;

public class MobEventSaltyTree extends MobEventYule {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventSaltyTree(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
        super.onSpawn(entity);
        entity.setCustomNameTag("Salty Tree");
	}
}
