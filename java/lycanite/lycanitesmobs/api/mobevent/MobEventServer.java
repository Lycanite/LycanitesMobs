package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class MobEventServer {
    public static boolean testOnCreative = false;

    // Properties:
    /** The MobEvent to play from. **/
	public MobEventBase mobEvent;
    /** Increases every tick that this event is active. **/
    public int ticks = 0;
    /** The world that this event is active in. **/
    public World world;
    /** The world time that this event started at. **/
    public long startedWorldTime = 0;


	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventServer(MobEventBase mobEvent, World world) {
		this.mobEvent = mobEvent;
        this.world = world;
        if(world.isRemote)
            LycanitesMobs.printWarning("", "Created a MobEventServer with a client side world, this shouldn't happen, things are going to get weird!");
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart() {
        this.mobEvent.onStart(this.world);

        // Check If Already Active On World:
        boolean extended = false;
        if(this.world != null) {
            if(this.world.getTotalWorldTime() < (this.startedWorldTime + this.mobEvent.duration)) {
                extended = true;
            }
        }

        this.startedWorldTime = world.getTotalWorldTime();
        this.ticks = 0;

        LycanitesMobs.printInfo("", "Mob Event " + (extended ? "Extended" : "Started") + ": " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + (this.mobEvent.duration / 20) + "secs");
    }

    public void changeStartedWorldTime(long newStartedTime) {
        this.startedWorldTime = newStartedTime;
        LycanitesMobs.printInfo("", "Mob Event Start Time Changed: " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + (this.mobEvent.duration / 20) + "secs" + " Time Remaining: " + ((this.mobEvent.duration - (this.world.getTotalWorldTime() - this.startedWorldTime)) / 20) + "secs");
    }


    // ==================================================
    //                      Finish
    // ==================================================
    public void onFinish() {
        this.mobEvent.onFinish(this.world);
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

        // Spawn Near Players:
        for(Object playerObj : this.world.playerEntities) {
            if(playerObj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)playerObj;
                if(!player.capabilities.isCreativeMode || testOnCreative) {

                    // Event Mob Spawning:
                    int tickOffset = 0;
                    for(SpawnTypeBase spawnType : this.mobEvent.spawners) {
                        spawnType.spawnMobs(this.ticks - tickOffset, this.world, player.getPosition(), player);
                        tickOffset += 7;
                    }
                }
            }
        }

        this.ticks++;

        // Stop Event When Time Runs Out:
        if(this.world.getTotalWorldTime() >= (this.startedWorldTime + this.mobEvent.duration)) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            worldExt.stopWorldEvent();
        }
    }
}
