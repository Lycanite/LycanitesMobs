package lycanite.lycanitesmobs.core.mobevent;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.Calendar;

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
		Calendar calendar = Calendar.getInstance();
		if(!Utilities.isHalloween())
			return false;
        return super.isEnabled();
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
