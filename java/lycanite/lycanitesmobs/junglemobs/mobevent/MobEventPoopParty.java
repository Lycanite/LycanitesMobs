package lycanite.lycanitesmobs.junglemobs.mobevent;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

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
		entity.worldObj.setBlock((int)entity.posX, (int)entity.posY, (int)entity.posZ, ObjectManager.getBlock("poopcloud"));
	}
}
