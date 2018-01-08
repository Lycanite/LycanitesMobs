package com.lycanitesmobs.core.mobevent;

import com.google.gson.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


public class MobEventManager extends JSONLoader {
	// Global:
	protected static MobEventManager INSTANCE;
    
    // Mob Events:
    public Map<String, MobEvent> mobEvents = new HashMap<>();
	public List<MobEventSchedule> mobEventSchedules = new ArrayList<>();

    // Properties:
    public boolean mobEventsEnabled = true;
    public boolean mobEventsRandom = false;
	/** The default temporary time applied to mobs spawned from events, where it will forcefully despawn after the specified time (in ticks). MobSpawns can override this. **/
	public int defaultMobDuration = 12000;
	public int minEventsRandomDay = 0;
	public int minTicksUntilEvent = 60 * 60 * 20;
	public int maxTicksUntilEvent = 120 * 60 * 20;

    protected long lastEventUpdateTime = 0;


	/** Returns the main Mob Event Manager instance. **/
	public static MobEventManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MobEventManager();
		}
		return INSTANCE;
	}


    /** Called during early start up, loads all global event configs into the manager. **/
	public void loadConfig() {
		ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        config.setCategoryComment("Global", "These are various settings that apply to all events.");
        //config.setCategoryComment("World", "These are various settings that apply to events on a per world basis. If your required world doesn't have its config values generated yet, you can generate them by entering the world in gae at least once.");

        this.mobEventsEnabled = config.getBool("Global", "Mob Events Enabled", this.mobEventsEnabled, "Set to false to completely disable the entire event system for every world.");
		this.mobEventsRandom = config.getBool("Global", "Random Mob Events Enabled", this.mobEventsRandom, "Set to false to disable random mob events for every world.");
		this.defaultMobDuration = config.getInt("Global", "Default Mob Duration", this.defaultMobDuration, "The default temporary time applied to mobs spawned from events, where it will forcefully despawn after the specified time (in ticks). MobSpawns can override this.");

		this.minEventsRandomDay = config.getInt("Global", "Default Random Mob Events Day Minimum", this.minEventsRandomDay, "If random events are enabled, they wont occur until this day is reached. Set to 0 to have random events enabled from the start of a world.");

		this.minTicksUntilEvent = config.getInt("Global", "Default Min Ticks Until Random Event", this.minTicksUntilEvent, "Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
		this.maxTicksUntilEvent = config.getInt("Global", "Default Max Ticks Until Random Event", this.maxTicksUntilEvent, "Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
	}


	/** Loads all JSON Mob Events. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
		LycanitesMobs.printDebug("MobEvents", "Loading JSON Mob Events!");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> mobEventJSONs = new HashMap<>();

		// Load Default Mob Events:
		Path path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.filename, "mobevents");
		Map<String, JsonObject> defaultMobEventJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultMobEventJSONs, "name", "event");

		// Load Mob Events:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customDir = new File(configPath + "mobevents");
		customDir.mkdirs();
		path = customDir.toPath();
		Map<String, JsonObject> customMobEventJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customMobEventJSONs, "name", "event");


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultMobEventJSONs, customMobEventJSONs, mobEventJSONs, true, "mobevents");


		// Create Mob Events:
		LycanitesMobs.printDebug("MobEvents", "Loading " + mobEventJSONs.size() + " Mob Events...");
		for(String spawnerJSONName : mobEventJSONs.keySet()) {
			try {
				JsonObject spawnerJSON = mobEventJSONs.get(spawnerJSONName);
				LycanitesMobs.printDebug("MobEvents", "Loading Mob Event JSON: " + spawnerJSON);
				MobEvent mobEvent = new MobEvent();
				mobEvent.loadFromJSON(spawnerJSON);
				this.addMobEvent(mobEvent);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Mob Event: " + spawnerJSONName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Mob Event: " + spawnerJSONName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("MobEvents", "Complete! " + this.mobEvents.size() + " JSON Mob Events Loaded In Total.");


		// Load Scheduled Events:
		this.mobEventSchedules.clear();
		Path defaultSchedulePath = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.filename, "mobeventschedule.json");
		JsonObject defaultScheduleJson = this.loadJsonObject(gson, defaultSchedulePath);

		File customScheduleFile = new File(configPath + "mobeventschedule.json");
		JsonObject customScheduleJson = null;
		if(customScheduleFile.exists()) {
			customScheduleJson = this.loadJsonObject(gson, customScheduleFile.toPath());
		}

		JsonObject scheduleJson = this.writeDefaultJSONObject(gson, "mobeventschedule", defaultScheduleJson, customScheduleJson);
		if(scheduleJson.has("schedules")) {
			JsonArray jsonArray = scheduleJson.get("schedules").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject scheduleEntryJson = jsonIterator.next().getAsJsonObject();
				MobEventSchedule mobEventSchedule = MobEventSchedule.createFromJSON(scheduleEntryJson);
				this.mobEventSchedules.add(mobEventSchedule);
			}
		}
		if(this.mobEventSchedules.size() > 0) {
			LycanitesMobs.printDebug("MobEvents", "Loaded " + this.mobEventSchedules.size() + " Mob Event Schedules.");
		}
	}


	/** Reloads all JSON Mob Events. **/
	public void reload() {
		LycanitesMobs.printDebug("MobEvents", "Destroying JSON Mob Events!");
		for(MobEvent mobEvent : this.mobEvents.values().toArray(new MobEvent[this.mobEvents.size()])) {
			mobEvent.destroy();
		}

		this.loadAllFromJSON(LycanitesMobs.group);
	}


    /**
     * Adds the provided Mob Event.
	 * @param mobEvent The Mob Event instance to add.
    **/
    public void addMobEvent(MobEvent mobEvent) {
        if(mobEvent == null)
            return;
        this.mobEvents.put(mobEvent.name, mobEvent);
        try {
			AssetManager.addSound("mobevent_" + mobEvent.title.toLowerCase(), LycanitesMobs.group, "mobevent." + mobEvent.title.toLowerCase());
		}
		catch(Exception e) {}
    }


	/** Removes a Mob Event from this Manager. **/
	public void removeMobEvent(MobEvent mobEvent) {
		if(!this.mobEvents.containsKey(mobEvent.name)) {
			LycanitesMobs.printWarning("", "[MobEvents] Tried to remove a Mob Event that hasn't been added: " + mobEvent.name);
			return;
		}
		this.mobEvents.remove(mobEvent.name);
	}


	/**
	 * Gets a Mob Event by name.
	 * @return Null if the event does not exist.
	 **/
	public MobEvent getMobEvent(String mobEventName) {
		if(!this.mobEvents.containsKey(mobEventName)) {
			return null;
		}
		return this.mobEvents.get(mobEventName);
	}


	/** Called every tick in a world and updates any active Server Side Mob Event players. **/
	@SubscribeEvent
	public void onWorldUpdate(WorldTickEvent event) {
		World world = event.world;
		if(world.isRemote)
			return;
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		if(worldExt == null)
			return;

		// Only Tick On World Time Ticks:
		if(this.lastEventUpdateTime == world.getTotalWorldTime())
			return;
		this.lastEventUpdateTime = world.getTotalWorldTime();
		worldExt.lastEventUpdateTime = world.getTotalWorldTime();

		// Only Run If Players Are Present:
		if(world.playerEntities.size() < 1) {
			return;
		}

		// Update World Mob Event Player:
		if(worldExt.serverWorldEventPlayer != null) {
			worldExt.serverWorldEventPlayer.onUpdate();
		}

        // Update Mob Event Players:
        if(worldExt.serverMobEventPlayers.size() > 0) {
            for (MobEventPlayerServer mobEventPlayerServer : worldExt.serverMobEventPlayers.values().toArray(new MobEventPlayerServer[worldExt.serverMobEventPlayers.size()])) {
                mobEventPlayerServer.onUpdate();
            }
        }
    }


	/** Updates the client side mob event players if active in the player's current world. **/
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent event) {
		if(LycanitesMobs.proxy.getClientPlayer() == null)
			return;

        World world = LycanitesMobs.proxy.getClientPlayer().getEntityWorld();
		if(!world.isRemote)
			return;
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(worldExt == null)
        	return;

		// Update Mob Event Players:
        for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values()) {
            mobEventPlayerClient.onUpdate();
        }

		// Update World Mob Event Player:
		if(worldExt.clientWorldEventPlayer != null) {
			worldExt.clientWorldEventPlayer.onUpdate();
		}
	}
}
