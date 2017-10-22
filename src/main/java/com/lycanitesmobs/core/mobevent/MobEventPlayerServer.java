package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobEventPlayerServer {
    public static boolean testOnCreative = false;

    // Properties:
    /** The MobEvent to play from. **/
	public MobEvent mobEvent;

    /** Increases every tick that this event is active. **/
    public int ticks = 0;

    /** The world that this event is active in. **/
    public World world;

    /** The world time that this event started at. **/
    public long startedWorldTime = 0;

    /** The player that triggered this event. Can be null as not all events are player specific. **/
    public EntityPlayer player;

    /** The origin position of this event. This is not always relevant. **/
    public BlockPos origin = new BlockPos(0, 0, 0);

    /** The level of the mob event, higher levels are more difficult, will spawn more subspecies, have higher mob levels, etc. **/
    public int level = 1;

	/** True if the started event was already running and show display as 'Event Extended' in chat. **/
	public boolean extended = false;


	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventPlayerServer(MobEvent mobEvent, World world) {
		this.mobEvent = mobEvent;
        this.world = world;
        if(world.isRemote)
            LycanitesMobs.printWarning("", "Created a MobEventServer with a client side world, this shouldn't happen, things are going to get weird!");
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart() {
        this.startedWorldTime = world.getTotalWorldTime();
        this.ticks = 0;

        LycanitesMobs.printInfo("", "Mob Event " + (this.extended ? "Extended" : "Started") + ": " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + (this.mobEvent.duration / 20) + "secs");
    }

    public void changeStartedWorldTime(long newStartedTime) {
        this.startedWorldTime = newStartedTime;
        LycanitesMobs.printInfo("", "Mob Event Start Time Changed: " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + (this.mobEvent.duration / 20) + "secs" + " Time Remaining: " + ((this.mobEvent.duration - (this.world.getTotalWorldTime() - this.startedWorldTime)) / 20) + "secs");
    }


    // ==================================================
    //                      Finish
    // ==================================================
    public void onFinish() {
        LycanitesMobs.printInfo("", "Mob Event Finished: " + this.mobEvent.getTitle());
    }


    // ==================================================
    //                      Update
    // ==================================================
    public void onUpdate() {
        if(this.world == null) {
            LycanitesMobs.printWarning("", "MobEventBase was trying to update without a world object, stopped!");
            return;
        }
        else if(this.world.isRemote) {
            LycanitesMobs.printWarning("", "MobEventBase was trying to update with a client side world, stopped!");
            return;
        }

        this.mobEvent.onUpdate(this.world, this.player, this.origin, this.level, this.ticks);
        this.ticks++;

        // Stop Event When Time Runs Out:
        if(this.ticks >= this.mobEvent.duration) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            if("world".equalsIgnoreCase(this.mobEvent.channel)) {
            	worldExt.stopWorldEvent();
			}
			else {
				worldExt.stopMobEvent(this.mobEvent.name);
			}
        }
    }
}
