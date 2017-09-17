package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.world.World;

public class MobEventServerBoss extends MobEventServer {
    public MobEventBoss mobEventBoss;
    public int originX = 0;
    public int originY = 0;
    public int originZ = 0;

	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventServerBoss(MobEventBoss mobEventBoss, World world) {
        super(mobEventBoss, world);
        this.mobEventBoss = mobEventBoss;
    }


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart() {
        this.mobEvent.onStart(this.world, this.rank);

        this.startedWorldTime = world.getTotalWorldTime();
        this.ticks = 0;

        LycanitesMobs.printInfo("", "Boss Event " + "Started" + ": " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Intro Duration: " + (this.mobEvent.duration / 20) + "secs");
    }

    public void changeStartedWorldTime(long newStartedTime) {
        this.startedWorldTime = newStartedTime;
        LycanitesMobs.printInfo("", "Boss Event Start Time Changed: " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Intro Duration: " + (this.mobEvent.duration / 20) + "secs" + " Time Remaining: " + ((this.mobEvent.duration - (this.world.getTotalWorldTime() - this.startedWorldTime)) / 20) + "secs");
    }


    // ==================================================
    //                      Finish
    // ==================================================
    @Override
    public void onFinish() {
        this.mobEvent.onFinish(this.world, this.rank);
        LycanitesMobs.printInfo("", "Mob Event Finished: " + this.mobEvent.getTitle());
    }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void onUpdate() {
        if(this.world == null) {
            LycanitesMobs.printWarning("", "MobEventServerBoss was trying to update without a world object, stopped!");
            return;
        }
        else if(this.world.isRemote) {
            LycanitesMobs.printWarning("", "MobEventServerBoss was trying to update with a client side world, stopped!");
            return;
        }

        this.mobEventBoss.bossSetup(this.ticks, this.world, this.originX, this.originY, this.originZ, this.rank);
        this.ticks++;

        // Stop Event When Time Runs Out:
        if(this.world.getTotalWorldTime() >= (this.startedWorldTime + this.mobEvent.duration)) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            worldExt.stopMobEvent(this);
        }
    }
}
