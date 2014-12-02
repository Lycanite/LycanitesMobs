package lycanite.lycanitesmobs.api.mobevent;

import java.util.Calendar;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
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
		Calendar calendar = Calendar.getInstance();
		if(!Utilities.isHalloween())
			return false;
        return super.isEnabled();
    }


    // ==================================================
    //                       Start
    // ==================================================
	@Override
    public void onStart(World world) {
        super.onStart(world);
    }


    // ==================================================
    //                      Finish
    // ==================================================
	@Override
    public void onFinish() {
        super.onFinish();
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
	}
}
