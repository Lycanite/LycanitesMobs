package lycanite.lycanitesmobs.demonmobs.mobevent;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventBoss;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventRahovart extends MobEventBoss {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventRahovart(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
		world.getWorldInfo().setRaining(false);
		world.getWorldInfo().setThundering(false);
    }


    // ==================================================
    //                     Boss Setup
    // ==================================================
    /** This is the main boss setup, this will create the arena, decorate it, move players and finally, summon the boss. The time value is used to determine what to do. **/
    public void bossSetup(int time, World world, int originX, int originY, int originZ) {
        if(time == 1 * 20) {
            // TODO: Build arena.
        }

        if(time == 5 * 20) {
            // TODO: Add pillars.
        }

        if(time == 7 * 20) {
            // TODO: Add hellfire.
        }

        if(time == 9 * 20) {
            // TODO: Summon Rahovart.
        }
    }
}
