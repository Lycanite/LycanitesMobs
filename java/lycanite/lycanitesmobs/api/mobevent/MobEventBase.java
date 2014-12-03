package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.config.ConfigSpawning.SpawnDimensionSet;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
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
    public boolean forceNoDespawn = true;
    public int minDay = 0;
    public int duration = 60 * 20;
    public int mobDuration = 10 * 60 * 20;
	
	// Dimensions:
    /** A comma separated list of dimensions that this event can occur in. As read from the config **/
    public String dimensionEntries = "-1, 1";
	/** A blacklist of dimension IDs (changes to whitelist if dimensionWhitelist is true) that this event can occur in. **/
	public int[] dimensionBlacklist;
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes;
	/** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
	public boolean dimensionWhitelist = false;

    // Active:
    public int ticks = 0;
    public World world;
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name, GroupInfo group) {
		this.name = name;
		this.group = group;
	}
    
	
    // ==================================================
    //                       Config
    // ==================================================
    /** Makes this event read the config. **/
	public void loadFromConfig() {
		ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
		this.duration = config.getInt("Event Durations", this.name, this.duration);
		this.mobDuration = config.getInt("Event Mob Durations", this.name, this.mobDuration);
        this.forceSpawning = config.getBool("Event Forced Spawning", this.name, this.forceSpawning);
        this.forceNoDespawn = config.getBool("Event Forced No Despawning", this.name, this.forceSpawning);
		this.minDay = config.getInt("Event Day Minimums", this.name, this.minDay);
        
		// Event Dimensions:
        config.setCategoryComment("Event Dimensions", "Sets which dimensions (by ID) that this event WILL NOT occur in. However if 'Spawn Dimensions Whitelist Mode' is set to true, it will instead set which dimensions that this event WILL ONLY occur in. Multiple entries should be comma separated.");
        SpawnDimensionSet eventDimensions = config.getDimensions("Event Dimensions", this.name + " Dimensions", this.dimensionEntries);
        this.dimensionBlacklist = eventDimensions.dimensionIDs;
        this.dimensionTypes = eventDimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("Event Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
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
		ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
		return config.getBool("Events Enabled", this.name, true);
	}
	
	
    // ==================================================
    //                    Can Start
    // ==================================================
	/*
	 * Returns true if this event is able to start on the provided extended world.
	 */
	public boolean canStart(World world, ExtendedWorld worldExt) {
		if(world.provider == null)
			return false;
		
		boolean validDimension = false;
		// Check Types:
		for(String eventDimensionType : this.dimensionTypes) {
    		if("ALL".equalsIgnoreCase(eventDimensionType)) {
    			validDimension = true;
    		}
    		else if("VANILLA".equalsIgnoreCase(eventDimensionType)) {
    			validDimension = world.provider.dimensionId > -2 && world.provider.dimensionId < 2;
    		}
    	}
		
		// Check IDs:
		if(!validDimension) {
			validDimension =  !this.dimensionWhitelist;
	    	for(int eventDimension : this.dimensionBlacklist) {
	    		if(world.provider.dimensionId == eventDimension) {
	    			validDimension = this.dimensionWhitelist;
	    			break;
	    		}
	    	}
		}
		    
		return validDimension && Math.floor(worldExt.getOverallEventTime() / 24000D) >= this.minDay;
	}


    // ==================================================
    //                    Event Stats
    // ==================================================
	/* Sets the default dimensions for this event. */
    public MobEventBase setDimensions(String string) {
        this.dimensionEntries = string;
        return this;
    }
    
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
        if(!this.spawners.contains(spawner) && spawner.hasSpawns()) {
            this.spawners.add(spawner);
            spawner.setMobEvent(this);
        }
        return this;
    }
    
    public boolean hasSpawners() {
    	return this.spawners.size() > 0;
    }
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(World world) {
		LycanitesMobs.printInfo("", "Mob Event Started: " + this.getTitle() + " in Dimension: " + world.provider.dimensionId);
		
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
	                    spawnType.spawnMobs(this.ticks - tickOffset, this.world, x, y, z, player);
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
	public void onSpawn(EntityLiving entity) {
		if(entity instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
			entityCreature.setTemporary(this.mobDuration);
		}
	}
}
