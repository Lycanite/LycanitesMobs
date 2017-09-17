package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.world.World;

public class MobEventYule extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventYule(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                     Can Start
    // ==================================================
	@Override
    public boolean canStart(World world, ExtendedWorld worldExt) {
		if(!Utilities.isYuletide())
			return false;
        return super.isEnabled();
    }
}
