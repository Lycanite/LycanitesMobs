package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.config.ConfigSpawning.SpawnDimensionSet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MobEventBase {
    // Global Properties:
    public static boolean canAffectWeather = true;
    public static boolean canAffectTime = true;

	// Properties:
	public String name = "mobevent";
	public int weight = 8;
    public List<SpawnTypeBase> spawners = new ArrayList<SpawnTypeBase>();
    public GroupInfo group;
    public boolean forceSpawning = true;
    public boolean forceNoDespawn = true;
    public int minDay = 0;
    public int firstScheduleDay = -1;
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


    // ==================================================
    //                 Global Settings
    // ==================================================
    public static void loadGlobalSettings() {
        ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        canAffectWeather = config.getBool("Global Event Settings", "Affect Weather", canAffectWeather);
        canAffectTime = config.getBool("Global Event Settings", "Affect Time", canAffectTime);
    }
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name, GroupInfo group) {
		this.name = name;
		this.group = group;

        AssetManager.addSound("mobevent_" + this.name.toLowerCase(), this.group, "mobevent." + this.name.toLowerCase());
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
        this.forceNoDespawn = config.getBool("Event Forced No Despawning", this.name, this.forceNoDespawn);
		this.minDay = config.getInt("Event Day Minimums", this.name, this.minDay);
        
		// Event Dimensions:
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
		return I18n.translateToLocal("mobevent." + this.name + ".name");
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
    //                      Can Start
    // ==================================================
	/*
	 * Returns true if this event is able to start on the provided extended world.
	 */
	public boolean canStart(World world, ExtendedWorld worldExt) {
		if(world.provider == null || !this.hasSpawners())
			return false;
        if(worldExt.mobEventsLocked && !worldExt.mobEventsLockedOnlyOnSchedule && this.firstScheduleDay < 0)
            return false;
		
		boolean validDimension = false;
		// Check Types:
		for(String eventDimensionType : this.dimensionTypes) {
    		if("ALL".equalsIgnoreCase(eventDimensionType)) {
    			validDimension = true;
    		}
    		else if("VANILLA".equalsIgnoreCase(eventDimensionType)) {
    			validDimension = world.provider.getDimension() > -2 && world.provider.getDimension() < 2;
    		}
    	}
		
		// Check IDs:
		if(!validDimension) {
			validDimension =  !this.dimensionWhitelist;
	    	for(int eventDimension : this.dimensionBlacklist) {
	    		if(world.provider.getDimension() == eventDimension) {
	    			validDimension = this.dimensionWhitelist;
	    			break;
	    		}
	    	}
		}

        int currentDay = (int)Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000D);
        int minimumRandomDay = this.minDay;
        if(worldExt.mobEventsLocked)
            minimumRandomDay = Math.max(minimumRandomDay, this.firstScheduleDay);
        return validDimension && currentDay >= minimumRandomDay;
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
        if(world.getDifficulty().getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.getDifficulty().getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.75F);
    }

    /* Returns the distance from the player that event mobs are spawned from. This changes based on the difficulty of the provided world. */
    public int getRange(World world) {
        int base = MobEventManager.instance.baseRange;
        if(world.getDifficulty().getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.getDifficulty().getDifficultyId() == 2)
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
    //               Start and Finish Effects
    // ==================================================
    public void onStart(World world, int rank) { }
    public void onFinish(World world, int rank) { }


    // ==================================================
    //                   Spawn Effects
    // ==================================================
    public void onSpawn(EntityLiving entity, int rank) {
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.setTemporary(this.mobDuration);
        }
    }
	

    // ==================================================
    //                  Get Event Players
    // ==================================================
    public MobEventServer getServerEvent(World world) {
        return new MobEventServer(this, world);
    }
    public MobEventClient getClientEvent(World world) {
        return new MobEventClient(this, world);
    }
}
