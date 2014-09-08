package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.network.MessageMobEvent;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;


public class MobEventManager {
	// Global:
    public static MobEventManager instance;
    public static boolean mobEventsEnabled = true;
    public static int minTicksUntilEvent = 20 * 60 * 20;
    public static int maxTicksUntilEvent = 40 * 60 * 20;
    
    // Mob Events:
    public Map<String, MobEventBase> worldMobEvents = new HashMap<String, MobEventBase>();
    public MobEventBase activeMobEvent = null;
    
    // World Counts:
    public Map<World, Integer> worldCounts = new HashMap<World, Integer>();
    public Map<World, Integer> worldTargets = new HashMap<World, Integer>();

    // Properties:
    public int baseRate = 120;
    public int baseRange = 32;
    

    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventManager() {
		instance = this;
	}


    // ==================================================
    //                  Load Mob Events
    // ==================================================
	/** Called during start up, loads all global events and config settings into the manager. **/
	public void loadMobEvents() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		config.getBool("Global", "Mob Events Enabled", mobEventsEnabled, "If false, all mob events will be completely disabled.");
		config.setCategoryComment("Events Enabled", "Here each event can be turned on or off (true or false).");
	}


    // ==================================================
    //                 Add Mob Event
    // ==================================================
    /**
     * Adds the provided World Mob Event.
     *  **/
    public void addWorldEvent(MobEventBase mobEvent) {
        if(mobEvent != null)
            this.worldMobEvents.put(mobEvent.name, mobEvent);
    }


    // ==================================================
    //                    Event Stats
    // ==================================================
    /* Returns the rate that event mobs are spawned at in ticks. This changes based on the difficulty of the provided world. */
    public int getBaseRate() {
        return this.baseRate;
    }

    /* Returns the distance from the player that event mobs are spawned from. This changes based on the difficulty of the provided world. */
    public int getBaseRange() {
        return this.baseRange;
    }


    // ==================================================
    //                 World Update Event
    // ==================================================
	/** Called every tick in a world and counts down to the next event then fires it! The countdown is paused during an event. **/
	@SubscribeEvent
	public void onWorldUpdate(WorldTickEvent event) {
		if(event.type != TickEvent.Type.WORLD)
			return;
		if(event.side.isClient())
			return;

        // Get World:
        World world = event.world;
        if(world == null)
            return;
		
		// Update Active Event and Return:
		if(this.activeMobEvent != null) {
			this.activeMobEvent.onUpdate(world);
			return;
		}
		
		// Get Count and Target:
		if(!this.worldCounts.containsKey(world))
			this.worldCounts.put(world, 0);
		int count = this.worldCounts.get(world) + 1;
		
		if(!this.worldTargets.containsKey(world))
			this.worldTargets.put(world, this.getRandomEventDelay(world.rand));
		int target = this.worldTargets.get(world);
        if(target <= 0) {
            target = this.getRandomEventDelay(world.rand);
            this.worldTargets.put(world, target);
        }
		
		// Check Count and Start Event:
		if(count >= target) {
			MobEventBase newEvent = this.getRandomWorldMobEvent(world);
			if(newEvent != null)
                this.startMobEvent(newEvent, world);
			count = 0;
			this.worldTargets.put(world, this.getRandomEventDelay(world.rand));
		}
		this.worldCounts.put(world, count);
	}


    // ==================================================
    //                 Random Mob Events
    // ==================================================
	/**
	 * Returns a random world-based event for the given world.
	 * @return Returns a an appropriate weighted random Mob Event or null if none are available.
	 *  **/
	public MobEventBase getRandomWorldMobEvent(World world) {
		int dimensionID = 0;
		if(world.provider != null)
			dimensionID = world.provider.dimensionId;
		
		int totalWeights = 0;
		for(MobEventBase mobEventEntry : this.worldMobEvents.values()) {
			if(mobEventEntry.isEnabled())
				totalWeights += mobEventEntry.weight;
		}
		if(totalWeights <= 0)
			return null;
		
		int randomWeight = world.rand.nextInt(totalWeights);
		MobEventBase mobEvent = null;
		for(MobEventBase mobEventEntry : this.worldMobEvents.values()) {
			if(mobEventEntry.isEnabled()) {
				mobEvent = mobEventEntry;
				if(mobEventEntry.weight > randomWeight)
					break;
			}
		}
		
		return mobEvent;
	}


    // ==================================================
    //                Random Event Delay
    // ==================================================
	/** Gets a random time until a next event will fire. **/
	public int getRandomEventDelay(Random random) {
		int max = Math.max(20, maxTicksUntilEvent);
		int min = Math.max(20, minTicksUntilEvent);
		if(max < min) max = min;
		
		if(max == min)
			return min;
		
		return min + random.nextInt(max - min);
	}


    // ==================================================
    //                 Start Mob Event
    // ==================================================
    /**
     * Starts the provided Mob Event (provided by instance) on the provided world.
     *  **/
    public void startMobEvent(MobEventBase mobEvent, World world) {
        this.activeMobEvent = mobEvent;
        if(this.activeMobEvent != null)
            this.activeMobEvent.onStart(world);
        this.updateAllClients(world);
    }

    /**
     * Starts the provided Mob Event (provided by name) on the provided world.
     *  **/
    public void startMobEvent(String mobEventName, World world) {
        MobEventBase mobEvent = null;
        if(!"".equals(mobEventName) && this.worldMobEvents.containsKey(mobEventName))
            mobEvent = this.worldMobEvents.get(mobEventName);
        this.startMobEvent(mobEvent, world);
    }


    // ==================================================
    //                  Update Clients
    // ==================================================
    /** Sends a packet to all clients updating their world event. **/
    public void updateAllClients(World world) {
        if(world.isRemote || world.provider == null)
            return;
        MessageMobEvent message = new MessageMobEvent(this.activeMobEvent);
        LycanitesMobs.packetHandler.sendToDimension(message, world.provider.dimensionId);
    }
}
