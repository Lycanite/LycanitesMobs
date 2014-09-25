package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class MobEventBase {
	public static boolean testOnCreative = false;
	
	// Properties:
	public String name = "mobevent";
	public int weight = 8;
    public List<SpawnTypeBase> spawners = new ArrayList<SpawnTypeBase>();
    public GroupInfo group;
    public boolean forceSpawning = true;

    // Active:
    public int duration = 120 * 20;
    public int ticks = 0;
    public World world;
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name, GroupInfo group) {
		this.name = name;
		this.group = group;
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		this.duration = config.getInt("Event Durations", this.name, 120 * 60);
        this.forceSpawning = config.getBool("Event Forced Spawning", this.name, this.forceSpawning);
	}
    
	
    // ==================================================
    //                       Names
    // ==================================================
    /** Returns the translated name of this event. **/
	public String getTitle() {
		return StatCollector.translateToLocal("mobevent." + this.name + ".name");
	}

    /** Returns a translated string to overlay the event image, this returns an empty string for english as the image itself has the title in english. **/
    public String getDisplayTitle() {
        String title = this.getTitle().replaceAll(" ", "").toLowerCase();
        return title.equalsIgnoreCase(this.name) ? "" : title;
    }
	
	
    // ==================================================
    //                      Enabled
    // ==================================================
	public boolean isEnabled() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		return config.getBool("Events Enabled", this.name, true);
	}


    // ==================================================
    //                    Event Stats
    // ==================================================
    /* Returns the rate that event mobs are spawned at in ticks. This changes based on the difficulty of the provided world. */
    public int getRate(World world) {
        int base = MobEventManager.instance.baseRate;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.75F);
    }

    /* Returns the distance from the player that event mobs are spawned from. This changes based on the difficulty of the provided world. */
    public int getRange(World world) {
        int base = MobEventManager.instance.baseRange;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.75F);
    }


    // ==================================================
    //                     Spawners
    // ==================================================
    public MobEventBase addSpawner(SpawnTypeBase spawner) {
        if(!this.spawners.contains(spawner)) {
            this.spawners.add(spawner);
            spawner.setMobEvent(this);
        }
        return this;
    }
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(World world) {
		LycanitesMobs.printInfo("", "Mob Event Started: " + this.getTitle());
		
		this.world = world;
        this.ticks = 0;

        if(world.isRemote)
            LycanitesMobs.printWarning("", "Created a MobEventBase with a client side world, things are going to get strange!");
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish() {
		LycanitesMobs.printInfo("", "Mob Event Finished: " + this.getTitle());
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
	                int x = (int)player.posX;
	                int y = (int)player.posY;
	                int z = (int)player.posZ;
	
	                // Event Mob Spawning:
	                int tickOffset = 0;
	                for(SpawnTypeBase spawnType : this.spawners) {
	                    spawnType.spawnMobs(this.ticks - tickOffset, this.world, x, y, z);
	                    tickOffset += 7;
	                }
                }
            }
        }
        
        this.ticks++;

        // Stop Event When Time Runs Out:
        if(this.ticks > this.duration) {
        	MobEventManager.instance.stopMobEvent();
        }
	}


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    public MobEventClient getClientEvent(World world) {
        return new MobEventClient(this, world);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
	public void onSpawn(EntityLiving entity) {}
}
