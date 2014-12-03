package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.Calendar;

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
		Calendar calendar = Calendar.getInstance();
		if(!Utilities.isYuletide())
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
