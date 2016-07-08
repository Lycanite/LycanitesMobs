package lycanite.lycanitesmobs.junglemobs.mobevent;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
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
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);
		entity.worldObj.setBlockState(entity.getPosition(), ObjectManager.getBlock("poopcloud").getDefaultState());
	}
}
