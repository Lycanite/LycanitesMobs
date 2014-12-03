package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventSatanClaws extends MobEventYule {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventSatanClaws(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart(World world) {
        super.onStart(world);
        long currentTime = world.provider.getWorldTime();

        int dayTime = 24000;
        int targetTime = 20000;

        long excessTime = currentTime % dayTime;
        int addedTime = dayTime;
        if(excessTime > targetTime) {
            targetTime += dayTime;
        }

        world.provider.setWorldTime(currentTime - excessTime + targetTime);
    }


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
        super.onSpawn(entity);
        entity.setCustomNameTag("Satan Claws");
	}
}
