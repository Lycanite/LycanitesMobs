package com.lycanitesmobs.core.mobevent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MobEventManager extends JSONLoader {
	// Global:
    public static MobEventManager INSTANCE;
    
    // Mob Events:
    public Map<String, MobEventBase> allMobEvents = new HashMap<String, MobEventBase>();
    public Map<String, MobEventBase> worldMobEvents = new HashMap<String, MobEventBase>();
    public Map<String, Map<String, MobEventBase>> worldMobEventSets = new HashMap<String, Map<String, MobEventBase>>();

    // Properties:
    public boolean mobEventsEnabled = true;
    public boolean mobEventsRandom = false;
    public boolean mobEventsSchedule = true;
    public int baseRate = 10 * 20;
    public int baseRange = 32;
	public boolean canAffectWeather = true;
	public boolean canAffectTime = true;
	public boolean aggressiveEvents = false;


	// ==================================================
	//                   Get Instance
	// ==================================================
	public static MobEventManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MobEventManager();
		}
		return INSTANCE;
	}


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventManager() {
		INSTANCE = this;
	}


    // ==================================================
    //                  Load Mob Events
    // ==================================================
	/** Called during start up, loads all global events and config settings into the manager. **/
	public void loadConfig() {
		ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        config.setCategoryComment("Global", "These are various settings that apply to all events.");
        config.setCategoryComment("World", "These are various settings that apply to events on a per world basis. If your required world doesn't have its config values generated yet, you can generate them by entering the world in gae at least once.");

        this.mobEventsEnabled = config.getBool("Global", "Mob Events Enabled", this.mobEventsEnabled, "Set to false to completely disable the entire event system for every world.");
        this.mobEventsRandom = config.getBool("Global", "Random Mob Events Enabled", this.mobEventsRandom, "Set to false to disable random mob events for every world.");
        this.mobEventsSchedule = config.getBool("Global", "Mob Events Enabled", this.mobEventsSchedule, "Set to false to disable scheduled events for every world.");
        this.baseRate = config.getInt("Global", "Base Spawn Rate", this.baseRate, "Sets the base interval in ticks (20 ticks = 1 second) between each mob spawn, this is multiplied by 1.5 on easy and 0.5 on hard.");
        this.baseRange = config.getInt("Global", "Base Spawn Range", this.baseRange, "Sets the base range in blocks from each player/area that event mobs will spawn.");

		this.canAffectWeather = config.getBool("Global", "Affect Weather", this.canAffectWeather, "Set to false to prevent all events from changing the weather.");
		this.canAffectTime = config.getBool("Global", "Affect Time", this.canAffectTime, "Set to false to prevent all events from changing the time fo day.");
		this.aggressiveEvents = config.getBool("Global", "Aggressive Events", this.aggressiveEvents, "If set to true, all mobs spawned from events will be told to immediately fixate on the player.");

        config.setCategoryComment("Events Enabled", "Here each event can be turned on or off (true or false).");
        config.setCategoryComment("Events Mob Durations", "Here you can set the duration (in ticks where 20 ticks = 1 second) of each event.");
        config.setCategoryComment("Events Forced Spawning", "Sets which events force their mobs to spawn, forced spawns will ignore other mods that interfere with mob spawning.");
        config.setCategoryComment("Events Forced No Despawning", "Sets which events force their spawned mobs to not despawn naturally (like most vanilla monsters do). However, mobs spawned by events will always only last 10 minutes and will then be forcefully despawned unless they are tamed by players, given a name tag, etc.");
        config.setCategoryComment("Event Day Minimums", "The minimum day before each event can occur randomly. For example if Shadow Games is set to 10 then it wont ever occured as a random event until day 10. Note: If Schedules and Locked Random events are active, the random event will not occur until both the Minimum Event day set here and first Schedule is met (by default schedules and event locks aren't used).");
        config.setCategoryComment("Event Dimensions", "Sets which dimensions (by ID) that this event WILL NOT occur in. However if 'Spawn Dimensions Whitelist Mode' is set to true, it will instead set which dimensions that this event WILL ONLY occur in. Multiple entries should be comma separated.");

		this.loadAllFromJSON(LycanitesMobs.group);
	}

	/** Loads all JSON Equipment Parts. Should only be done on pre-init. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
		LycanitesMobs.printDebug("MobEvents", "Loading JSON Mob Events!");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> mobEventJSONs = new HashMap<>();

		// Load Default Parts:
		Path path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.filename, "equipment");
		Map<String, JsonObject> defaultEquipmentPartJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultEquipmentPartJSONs, "itemName");

		// Load Custom Parts:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customDir = new File(configPath + "equipment");
		customDir.mkdirs();
		path = customDir.toPath();
		Map<String, JsonObject> customEquipmentPartJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customEquipmentPartJSONs, "itemName");


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultEquipmentPartJSONs, customEquipmentPartJSONs, mobEventJSONs, false, "equipment");


		// Create Mob Events:
		LycanitesMobs.printDebug("MobEvents", "Loading " + mobEventJSONs.size() + " Equipment Parts...");
		for(String spawnerJSONName : mobEventJSONs.keySet()) {
			try {
				JsonObject spawnerJSON = mobEventJSONs.get(spawnerJSONName);
				LycanitesMobs.printDebug("MobEvents", "Loading Equipment Part JSON: " + spawnerJSON);
				MobEventBase mobEventBase = new MobEventBase(spawnerJSONName, groupInfo);
				mobEventBase.loadFromJSON(spawnerJSON);
				this.addMobEvent(mobEventBase);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Equipment Part: " + spawnerJSONName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Equipment Part: " + spawnerJSONName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("MobEvents", "Complete! " + this.allMobEvents.size() + " JSON Equipment Parts Loaded In Total.");
	}


    // ==================================================
    //                 Add Mob Event
    // ==================================================
    /**
     * Adds the provided Mob Event.
     *  **/
    public void addMobEvent(MobEventBase mobEvent) {
        if(mobEvent == null)
            return;
        mobEvent.loadFromConfig();
        this.allMobEvents.put(mobEvent.name, mobEvent);
    }


	// ==================================================
	//                 Get Mob Event
	// ==================================================
	/**
	 * Gets a Mob Event by name.
	 * @return Null if the event does not exist.
	 **/
	public MobEventBase getMobEvent(String mobEventName) {
		if(!this.allMobEvents.containsKey(mobEventName)) {
			return null;
		}
		return this.allMobEvents.get(mobEventName);
	}


    // ==================================================
    //                 Add World Event
    // ==================================================
    /**
     * Adds the provided World Mob Event, this will also then add it to the allMobEvents list too.
     *  **/
    public void addWorldEvent(MobEventBase mobEvent, String set) {
        if(mobEvent != null && mobEvent.hasSpawners()) {
            this.addMobEvent(mobEvent);
            if(!this.worldMobEventSets.containsKey(set))
                this.worldMobEventSets.put(set, new HashMap<>());
            this.worldMobEventSets.get(set).put(mobEvent.name, mobEvent);
            this.worldMobEvents.put(mobEvent.name, mobEvent);
        }
    }

    public void addWorldEvent(MobEventBase mobEvent) {
        this.addWorldEvent(mobEvent, "main");
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


        // ===== Update Mob Events =====
        if(worldExt.serverMobEvents.size() > 0) {
            for (MobEventServer mobEventServer : worldExt.serverMobEvents.toArray(new MobEventServer[worldExt.serverMobEvents.size()])) {
                mobEventServer.onUpdate();
            }
        }


        // ===== Update World Event =====

        // Check If Events Are Completely Disabled:
        if(!this.mobEventsEnabled || !worldExt.mobEventsEnabled || world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            if(worldExt.serverWorldEvent != null)
                worldExt.stopWorldEvent();
            return;
        }

        // Only Tick On World Time Ticks:
        if(worldExt.lastEventUpdateTime == world.getTotalWorldTime()) return;
        worldExt.lastEventUpdateTime = world.getTotalWorldTime();
		
		// Only Run If Players Are Present:
		if(world.playerEntities.size() < 1) {
			return;
		}

        // Scheduled World Events:
        if(this.mobEventsSchedule) {
            if(!worldExt.eventScheduleLoaded)
                worldExt.loadEventSchedule(worldExt.mobEventsSchedule);
            if(worldExt.eventSchedule != null && worldExt.eventSchedule.size() > 0) {
                MobEventBase newEvent = worldExt.getScheduledWorldMobEvent();
                if(newEvent != null) {
                    worldExt.startWorldEvent(newEvent);
                }
            }
        }

        // Update Active Event If Present and Return:
        if(worldExt.serverWorldEvent != null) {
            worldExt.serverWorldEvent.onUpdate();
            return;
        }

        // Random World Events:
        if(!this.mobEventsRandom || !worldExt.mobEventsRandom) return;
        if(worldExt.minEventsRandomDay > 0 && Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000D) < worldExt.minEventsRandomDay) return;
        if(worldExt.getWorldEventStartTargetTime() <= 0 || worldExt.getWorldEventStartTargetTime() > world.getTotalWorldTime() + worldExt.maxTicksUntilEvent) {
            worldExt.setWorldEventStartTargetTime(world.getTotalWorldTime() + worldExt.getRandomEventDelay(world.rand));
        }
        if(world.getTotalWorldTime() >= worldExt.getWorldEventStartTargetTime()) {
			MobEventBase newEvent = this.getRandomWorldMobEvent(world, worldExt);
			if(newEvent != null) {
                worldExt.startWorldEvent(newEvent);
			}
		}
    }
	
	
    // ==================================================
    //                 Client Update Event
    // ==================================================
	/** Updates the client side mob event INSTANCE if present in the player's current world. **/
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent event) {
		if(LycanitesMobs.proxy.getClientPlayer() == null)
			return;

        World world = LycanitesMobs.proxy.getClientPlayer().getEntityWorld();
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(!world.isRemote || worldExt == null) return;

        // Update Mob Events:
        for(MobEventClient mobEventClient : worldExt.clientMobEvents.values()) {
            mobEventClient.onUpdate();
        }

		// Update World Event:
		if(worldExt.clientWorldEvent != null) {
			worldExt.clientWorldEvent.onUpdate();
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
			dimensionID = world.provider.getDimension();
		
		// Seasonal Events:
		if(Utilities.isHalloween() && this.worldMobEvents.containsKey("halloween"))
			if(this.worldMobEvents.get("halloween").isEnabled() && this.worldMobEvents.get("halloween").canStart(world, worldExt))
				return this.worldMobEvents.get("halloween");
		/*if(Utilities.isNewYear() && this.worldMobEvents.containsKey("newyear"))
			if(this.worldMobEvents.get("newyear").isEnabled() && this.worldMobEvents.get("newyear").canStart(world, worldExt))
				return this.worldMobEvents.get("newyear");*/
		
		// Seasonal Event Sets:
        Map<String, MobEventBase> worldMobEventSet = this.worldMobEventSets.get("main");
        if(Utilities.isYuletide() && this.worldMobEventSets.containsKey("yule"))
            worldMobEventSet = this.worldMobEventSets.get("yule");

        // Get Events and Weights:
		List<MobEventBase> validMobEvents = new ArrayList<MobEventBase>();
		int totalWeights = 0;
		for(MobEventBase mobEventEntry : worldMobEventSet.values()) {
			if(mobEventEntry.isEnabled() && mobEventEntry.canStart(world, worldExt)) {
				totalWeights += mobEventEntry.weight;
				validMobEvents.add(mobEventEntry);
			}
		}
		if(totalWeights <= 0)
			return null;
		
		// Pick Random Event Using Weights:
		int randomWeight = 1;
		if(totalWeights > 1)
			randomWeight = world.rand.nextInt(totalWeights - 1) + 1;
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
}
