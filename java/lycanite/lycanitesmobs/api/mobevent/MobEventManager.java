package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.network.MessageMobEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;


public class MobEventManager {
	// Global:
    public static MobEventManager instance;
    public static boolean mobEventsEnabled = true;
    public static int minTicksUntilEvent = 20 * 60 * 20;
    public static int maxTicksUntilEvent = 40 * 60 * 20;
    
    // Mob Events:
    public Map<String, MobEventBase> worldMobEvents = new HashMap<String, MobEventBase>();
    public MobEventBase serverMobEvent = null;
    public MobEventClient clientMobEvent = null;

    // Properties:
    public int baseRate = 10 * 20;
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
		mobEventsEnabled = config.getBool("Global", "Mob Events Enabled", mobEventsEnabled, "If false, all mob events will be completely disabled.");
		minTicksUntilEvent = config.getInt("Global", "Min Ticks Until Event", minTicksUntilEvent, "Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
		maxTicksUntilEvent = config.getInt("Global", "Max Ticks Until Event", maxTicksUntilEvent, "Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
		baseRate = config.getInt("Global", "Base Spawn Rate", baseRate, "Sets the base interval in ticks (20 ticks = 1 second) between each mob spawn, this is multiplied by 1.5 on easy and 0.5 on hard.");
		baseRange = config.getInt("Global", "Base Spawn Range", baseRange, "Sets the base range in blocks from each player/area that event mobs will spawn.");
		
		config.setCategoryComment("Events Enabled", "Here each event can be turned on or off (true or false).");
	}


    // ==================================================
    //                 Add Mob Event
    // ==================================================
    /**
     * Adds the provided World Mob Event.
     *  **/
    public void addWorldEvent(MobEventBase mobEvent) {
        if(mobEvent != null) {
        	mobEvent.loadFromConfig();
            this.worldMobEvents.put(mobEvent.name, mobEvent);
        }
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
		World world = event.world;
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		if(world.isRemote || worldExt == null) return;
		if(!this.mobEventsEnabled || world.difficultySetting == EnumDifficulty.PEACEFUL) {
			if(this.serverMobEvent != null)
				this.stopMobEvent();
        	return;
        }
		
		// Only Run If Players Are Present:
		if(world.playerEntities.size() < 1) {
			return;
		}
		
		// Update Overall World Event Time:
		worldExt.setOverallEventTime(worldExt.getOverallEventTime() + 1);
		
		// Update Active Event and Return:
		if(this.serverMobEvent != null) {
			worldExt.setMobEventActiveTime(this.serverMobEvent.ticks + 1);
			this.serverMobEvent.onUpdate();
			return;
		}
		worldExt.setMobEventActiveTime(0);
		
		// Get Event Time:
		worldExt.setMobEventTime(worldExt.getMobEventTime() + 1);
		
		// Get Target Time:
        if(worldExt.getMobEventTarget() <= 0) {
        	worldExt.setMobEventTarget(this.getRandomEventDelay(world.rand));
        }
		
		// Check Count and Start Event:
		if(worldExt.getMobEventTime() >= worldExt.getMobEventTarget()) {
			MobEventBase newEvent = this.getRandomWorldMobEvent(world, worldExt);
			if(newEvent != null) {
                this.startMobEvent(newEvent, world);
			}
		}
	}
	
	
    // ==================================================
    //                 Client Update Event
    // ==================================================
	/** Called every tick in a world and counts down to the next event then fires it! The countdown is paused during an event. **/
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent event) {
		if(LycanitesMobs.proxy.getClientPlayer() == null)
			return;
		
		// Update Active Event and Return:
		if(this.clientMobEvent != null) {
			if(this.clientMobEvent.world == LycanitesMobs.proxy.getClientPlayer().worldObj)
				this.clientMobEvent.onUpdate();
			return;
		}
	}


    // ==================================================
    //                 Random Mob Events
    // ==================================================
	/**
	 * Returns a random world-based event for the given world.
	 * @return Returns a an appropriate weighted random Mob Event or null if none are available.
	 *  **/
	public MobEventBase getRandomWorldMobEvent(World world, ExtendedWorld worldExt) {
		int dimensionID = 0;
		if(world.provider != null)
			dimensionID = world.provider.dimensionId;
		
		// Seasonal Events:
		if(Utilities.isHalloween() && this.worldMobEvents.containsKey("halloween"))
			if(this.worldMobEvents.get("halloween").isEnabled() && this.worldMobEvents.get("halloween").canStart(worldExt))
				return this.worldMobEvents.get("halloween");
		
		// Get Events and Weights:
		List<MobEventBase> validMobEvents = new ArrayList<MobEventBase>();
		int totalWeights = 0;
		for(MobEventBase mobEventEntry : this.worldMobEvents.values()) {
			if(mobEventEntry.isEnabled() && mobEventEntry.canStart(worldExt)) {
				totalWeights += mobEventEntry.weight;
				validMobEvents.add(mobEventEntry);
			}
		}
		if(totalWeights <= 0)
			return null;
		
		// Pick Random Event Using Weights:
		int randomWeight = world.rand.nextInt(totalWeights);
		int searchWeight = 0;
		MobEventBase mobEvent = null;
		for(MobEventBase mobEventEntry : validMobEvents) {
			if(mobEventEntry.isEnabled()) {
				mobEvent = mobEventEntry;
				if(mobEventEntry.weight + searchWeight > randomWeight)
					break;
				searchWeight += mobEventEntry.weight;
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
		if(max <= min)
			return min;
		
		return min + random.nextInt(max - min);
	}


    // ==================================================
    //                 Stop Mob Event
    // ==================================================
    /**
     * Stops the Mob Event.
     *  **/
    public void stopMobEvent() {
        // Server Side:
        if(this.serverMobEvent != null) {
            this.serverMobEvent.onFinish();
            if(this.serverMobEvent.world != null) {
                ExtendedWorld worldExt = ExtendedWorld.getForWorld(this.serverMobEvent.world);
                worldExt.setMobEventType("");
            }
            this.serverMobEvent = null;
            this.updateAllClients();
        }

        // Client Side:
        if(this.clientMobEvent != null) {
            if(LycanitesMobs.proxy.getClientPlayer() != null)
            	this.clientMobEvent.onFinish(LycanitesMobs.proxy.getClientPlayer());
            this.clientMobEvent = null;
        }
    }


    // ==================================================
    //                 Start Mob Event
    // ==================================================
    /**
     * Starts the provided Mob Event (provided by instance) on the provided world.
     *  **/
    public void startMobEvent(MobEventBase mobEvent, World world) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null mob event, stopping any event instead.");
            this.stopMobEvent();
        }

        // Server Side:
        if(!world.isRemote) {
            this.serverMobEvent = mobEvent;
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            worldExt.setMobEventType(this.serverMobEvent.name);
            worldExt.setMobEventTime(0);
            worldExt.setMobEventTarget(0);
            this.serverMobEvent.onStart(world);
            this.updateAllClients();
        }

        // Client Side:
        if(world.isRemote) {
            this.clientMobEvent = mobEvent.getClientEvent(world);
            if(LycanitesMobs.proxy.getClientPlayer() != null)
            	this.clientMobEvent.onStart(world, LycanitesMobs.proxy.getClientPlayer());
        }
    }

    /**
     * Starts the provided Mob Event (provided by name) on the provided world.
     *  **/
    public void startMobEvent(String mobEventName, World world) {
        MobEventBase mobEvent = null;
        if(this.worldMobEvents.containsKey(mobEventName)) {
            mobEvent = this.worldMobEvents.get(mobEventName);
            if(!mobEvent.isEnabled())
            	mobEvent = null;
    	}
        else {
        	LycanitesMobs.printWarning("", "Tried to start an event with the invalid name: '" + mobEventName + "' on " + (world.isRemote ? "Client" : "Server"));
        	return;
        }
        
        this.startMobEvent(mobEvent, world);
    }


    // ==================================================
    //                  Update Clients
    // ==================================================
    /** Sends a packet to all clients updating their event. **/
    public void updateAllClients() {
        MessageMobEvent message = new MessageMobEvent(this.serverMobEvent);
        LycanitesMobs.packetHandler.sendToAll(message);
        
    }
}
