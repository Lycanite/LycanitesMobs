package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.world.World;

public class MobEventHalloween extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventHalloween(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                     Can Start
    // ==================================================
	@Override
    public boolean canStart(World world, ExtendedWorld worldExt) {
		if(!Utilities.isHalloween())
			return false;
        return super.isEnabled();
    }
}
