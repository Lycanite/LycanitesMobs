package com.lycanitesmobs.junglemobs.mobevent;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;

public class MobEventPoopParty extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventPoopParty(String name, GroupInfo group) {
		super(name, group);
	}
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity, int rank) {
		super.onSpawn(entity, rank);
		entity.getEntityWorld().setBlockState(entity.getPosition(), ObjectManager.getBlock("poopcloud").getDefaultState());
	}
}
