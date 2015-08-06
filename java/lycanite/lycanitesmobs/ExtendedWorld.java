package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.ValuePair;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.mobevent.*;
import lycanite.lycanitesmobs.api.network.MessageMobEvent;
import lycanite.lycanitesmobs.api.network.MessageWorldEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class ExtendedWorld extends WorldSavedData {
	public static String EXT_PROP_NAME = "LycanitesMobs";
	public static Map<World, ExtendedWorld> loadedExtWorlds = new HashMap<World, ExtendedWorld>();

    /** The world instance to work with. **/
	public World world;
    public long lastEventUpdateTime = 0;
    /** The last minute used when checking for scheduled events. **/
    public int lastEventScheduleMinute = -1;

    // Mob Events World Config:
    public boolean configLoaded = false;
    public boolean useTotalWorldTime = true;
    public int minTicksUntilEvent = 60 * 60 * 20;
    public int maxTicksUntilEvent = 120 * 60 * 20;
    public boolean mobEventsEnabled = true;
    public boolean mobEventsRandom = true;
    public String mobEventsSchedule = "";
    public boolean eventScheduleLoaded = false;
    public boolean mobEventsLocked = false;
    public boolean mobEventsLockedOnlyOnSchedule = true;
    public int minEventsRandomDay = 0;
	
	// Mob Events:
    public List<MobEventServer> serverMobEvents = new ArrayList<MobEventServer>();
    public Map<String, MobEventClient> clientMobEvents = new HashMap<String, MobEventClient>();
    public Map<ValuePair<Integer, Integer>, MobEventBase> eventSchedule;
    public MobEventServer serverWorldEvent = null;
    public MobEventClient clientWorldEvent = null;
	private long worldEventStartTargetTime = 0;
    private long worldEventLastStartedTime = 0;
	private String worldEventType = "";
	private int worldEventCount = -1;
	
	// ==================================================
    //                   Get for World
    // ==================================================
	public static ExtendedWorld getForWorld(World world) {
		if(world == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedWorld from a null World.");
			return null;
		}
        ExtendedWorld worldExt;
		
		// Already Loaded:
		if(loadedExtWorlds.containsKey(world)) {
            worldExt = loadedExtWorlds.get(world);
            return worldExt;
        }
		
		WorldSavedData worldSavedData = world.perWorldStorage.loadData(ExtendedWorld.class, EXT_PROP_NAME); //world.loadItemData(ExtendedWorld.class, EXT_PROP_NAME);
		if(worldSavedData != null) {
			worldExt = (ExtendedWorld)worldSavedData;
			worldExt.world = world;
			worldExt.init();
		}
		else {
			worldExt = new ExtendedWorld(world);
			world.perWorldStorage.setData(EXT_PROP_NAME, worldExt); //world.setItemData(EXT_PROP_NAME, worldExt);
		}

		loadedExtWorlds.put(world, worldExt);
		return worldExt;
	}
	
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public ExtendedWorld(String prop_name) {
		super(EXT_PROP_NAME);
	}
	public ExtendedWorld(World world) {
		super(EXT_PROP_NAME);
		this.world = world;
	}
	
	
	// ==================================================
    //                        Init
    // ==================================================
	public void init() {
        this.loadConfig();

        this.lastEventUpdateTime = this.world.getTotalWorldTime() - 1;
        int currentTotalMinutes = (int)Math.floor((this.useTotalWorldTime ? this.world.getTotalWorldTime() : this.world.getWorldTime()) / 1000D);
        this.lastEventScheduleMinute = currentTotalMinutes % 24;

		// Start Saved Event:
		if(!this.world.isRemote && !"".equals(this.getWorldEventType()) && this.serverWorldEvent == null) {
            long savedLastStartedTime = this.getWorldEventLastStartedTime();
			this.startWorldEvent(this.getWorldEventType());
			if(this.serverWorldEvent != null) {
                this.serverWorldEvent.changeStartedWorldTime(savedLastStartedTime);
            }
		}
	}


    // ==================================================
    //                     Load Config
    // ==================================================
    public void loadConfig() {
        ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        this.mobEventsEnabled = config.getBool("World", this.getConfigEntryName("Mob Events Enabled"), mobEventsEnabled, "If false, all mob events will be completely disabled.");

        this.mobEventsRandom = config.getBool("World", this.getConfigEntryName("Random Mob Events"), mobEventsRandom, "If false, mob events will no longer occur randomly but can still occur via other means such as by schedule. Set this to true if you want both random and scheduled events to take place and also take a look at 'Lock Random Mob Events' if doing so.");
        this.minEventsRandomDay = config.getInt("World", this.getConfigEntryName("Random Mob Events Day Minimum"), minEventsRandomDay, "If random events are enabled, they wont occur until this day is reached. Set to 0 to have random events enabled from the start of a world.");

        this.mobEventsSchedule = config.getString("World", this.getConfigEntryName("Mob Events Schedule"), mobEventsSchedule, "Here you can add a list of scheduled events, leave blank to disable. Each entry should be: 'eventname,day,time' multiple entries should be separated by semicolons. For eventnames use '/lm mobevent list' when in game. Day is the target day number starting from day 0, see 'Use Total World Time' for how the world time is checked. Time is the minute of the in game day that the event should occur (there is 20 minutes in a Minecraft day/night cycle use 0-19), this can also be random by typing 'random' as a value, note that you can only have 1 random scheduled event per day. You may add spaces anywhere as they will be ignored, also don't add the 'quotations'!");
        this.mobEventsLocked = config.getBool("World", this.getConfigEntryName("Lock Random Mob Events"), mobEventsLocked, "If true, mob events will not occur randomly until they have been started from a schedule (or if an active world's schedule would have started the event at least once in the past).");
        this.mobEventsLockedOnlyOnSchedule = config.getBool("World", this.getConfigEntryName("Lock Random Mob Events Only On Schedule"), mobEventsLockedOnlyOnSchedule, "Only used if 'Lock Random Mob Events' is set to true. If true, mob events that are scheduled will be locked from occuring randomly but mob events that aren't on a schedule will be unlocked. If false, all events that are not scheduled at least once will never occur randomly as they cannot be unlocked.");

        this.useTotalWorldTime = config.getBool("World", this.getConfigEntryName("Use Total World Time"), useTotalWorldTime, "If true, the hidden total world time will be used for random event minimum days and scheduled events, if false the current world time is used instead, the current time is the time shown to players however it will reset to 0 if the world time is change via '/time set 0' or other commands/mods.");

        this.minTicksUntilEvent = config.getInt("World", this.getConfigEntryName("Min Ticks Until Random Event"), minTicksUntilEvent, "Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
        this.maxTicksUntilEvent = config.getInt("World", this.getConfigEntryName("Max Ticks Until Random Event"), maxTicksUntilEvent, "Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.");

        this.configLoaded = true;
    }

    protected String getConfigEntryName(String name) {
        return name + " " + this.world.provider.getDimensionName() + " (" + this.world.provider.dimensionId + ")";
    }
	
	
	// ==================================================
    //                    Get Properties
    // ==================================================
	//public int getMobEventTime() { return this.mobEventTime; }
	public long getWorldEventStartTargetTime() { return this.worldEventStartTargetTime; }
    public long getWorldEventLastStartedTime() { return this.worldEventLastStartedTime; }
	public String getWorldEventType() { return this.worldEventType; }
	public int getWorldEventCount() { return this.worldEventCount; }
	
	
	// ==================================================
    //                    Set Properties
    // ==================================================
	public void setWorldEventStartTargetTime(long setLong) {
		if(this.worldEventStartTargetTime != setLong)
			this.markDirty();
		this.worldEventStartTargetTime = setLong;
        if(setLong > 0)
            LycanitesMobs.printDebug("MobEvents", "Next random mob will start after " + ((this.worldEventStartTargetTime - this.world.getTotalWorldTime()) / 20) + "secs.");
	}
    public void setWorldEventLastStartedTime(long setLong) {
        if(this.worldEventLastStartedTime != setLong)
            this.markDirty();
        this.worldEventLastStartedTime = setLong;
    }
	public void setWorldEventType(String setString) {
		if(!this.worldEventType.equals(setString))
			this.markDirty();
		this.worldEventType = setString;
	}
	public void increaseMobEventCount() {
		this.worldEventCount++;
	}


    // ==================================================
    //                Scheduled Events
    // ==================================================
    public void loadEventSchedule(String schedule) {
        this.eventScheduleLoaded = true;
        schedule = schedule.replace(" ", "");
        if("".equals(schedule))
            return;
        this.eventSchedule = new HashMap<ValuePair<Integer, Integer>, MobEventBase>();

        for(String scheduleEntry : schedule.split(";")) {
            String[] scheduleEntryParts = scheduleEntry.split(",");
            if(scheduleEntryParts.length >= 3) {
                if(!NumberUtils.isNumber(scheduleEntryParts[1])) continue;
                int scheduleEntryDay = Integer.parseInt(scheduleEntryParts[1]);

                int scheduleEntryMinute = -1;
                if(!"random".equals(scheduleEntryParts[2])) {
                    if (!NumberUtils.isNumber(scheduleEntryParts[2])) continue;
                    scheduleEntryMinute = Integer.parseInt(scheduleEntryParts[2]);
                }

                if(!MobEventManager.instance.worldMobEvents.containsKey(scheduleEntryParts[0])) continue;
                MobEventBase scheduleEntryEvent = MobEventManager.instance.worldMobEvents.get(scheduleEntryParts[0]);

                if(scheduleEntryEvent == null) continue;
                if(scheduleEntryDay < scheduleEntryEvent.firstScheduleDay || scheduleEntryEvent.firstScheduleDay < 0)
                    scheduleEntryEvent.firstScheduleDay = scheduleEntryDay;

                this.eventSchedule.put(new ValuePair<Integer, Integer>(scheduleEntryDay, scheduleEntryMinute), scheduleEntryEvent);
                LycanitesMobs.printDebug("MobEvents", "Added Event to Schedule: " + scheduleEntryEvent.getTitle() + " (" + scheduleEntryParts[0] + ") Starts On Day " + scheduleEntryDay + " At Minute " + (scheduleEntryMinute < 0 ? "Random" : scheduleEntryMinute));
            }
        }
    }

    public MobEventBase getScheduledWorldMobEvent() {
        if(eventSchedule == null)
            return null;

        int dimensionID = 0;
        if(this.world.provider != null)
            dimensionID = this.world.provider.dimensionId;

        int currentDay = (int)Math.floor((this.useTotalWorldTime ? this.world.getTotalWorldTime() : this.world.getWorldTime()) / 24000D);
        int currentMin = (int)((long)Math.floor((this.useTotalWorldTime ? this.world.getTotalWorldTime() : this.world.getWorldTime()) / 1200D) % 20);
        if(this.lastEventScheduleMinute != currentMin) {
            this.lastEventScheduleMinute = currentMin;
            LycanitesMobs.printDebug("MobEvents", "Checking world day and minute for scheduled event... Day " + currentDay + " Minute " + currentMin);
            if(currentMin == 0 && eventSchedule.containsKey(new ValuePair<Integer, Integer>(currentDay, -1))) {
                MobEventBase randomMinuteEvent = eventSchedule.get(new ValuePair<Integer, Integer>(currentDay, -1));
                int randomMin = currentMin + 1 + this.world.rand.nextInt(18 - currentMin);
                LycanitesMobs.printDebug("MobEvents", "Found a random event that needs assigned a minute (" + (randomMinuteEvent != null ? randomMinuteEvent.name : "null") + "), setting random minute to " + randomMin + " this should from 1 to 19.");
                eventSchedule.put(new ValuePair<Integer, Integer>(currentDay, randomMin), randomMinuteEvent);
                eventSchedule.remove(new ValuePair<Integer, Integer>(currentDay, -1));
            }
            if(eventSchedule.containsKey(new ValuePair<Integer, Integer>(currentDay, currentMin))) {
                MobEventBase scheduledEvent = eventSchedule.get(new ValuePair<Integer, Integer>(currentDay, currentMin));
                LycanitesMobs.printDebug("MobEvents", "Found a scheduled event (" + (scheduledEvent != null ? scheduledEvent.name : "null") + "), this event will now start...");
                return scheduledEvent;
            }
            LycanitesMobs.printDebug("MobEvents", "No scheduled event found for this day and minute.");
        }

        return null;
    }


    // ==================================================
    //                Random Event Delay
    // ==================================================
    /** Gets a random time until the next random event will start. **/
    public int getRandomEventDelay(Random random) {
        int max = Math.max(20, this.maxTicksUntilEvent);
        int min = Math.max(20, this.minTicksUntilEvent);
        if(max <= min)
            return min;

        return min + random.nextInt(max - min);
    }


    // ==================================================
    //                 Start World Event
    // ==================================================
    /**
     * Starts the provided World Event (provided by instance) on the provided world.
     *  **/
    public void startWorldEvent(MobEventBase mobEvent) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null world event, stopping any event instead.");
            this.stopWorldEvent();
            return;
        }

        // Server Side:
        if(!this.world.isRemote) {
            this.serverWorldEvent = mobEvent.getServerEvent(this.world);
            this.setWorldEventType(mobEvent.name);
            this.increaseMobEventCount();
            this.setWorldEventStartTargetTime(0);
            this.setWorldEventLastStartedTime(this.world.getTotalWorldTime());
            this.serverWorldEvent.onStart();
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.world.isRemote) {
            boolean extended = false;
            if(this.clientWorldEvent != null)
                extended = this.clientWorldEvent.mobEvent == mobEvent;
            this.clientWorldEvent = mobEvent.getClientEvent(this.world);
            this.clientWorldEvent.extended = extended;
            if(LycanitesMobs.proxy.getClientPlayer() != null)
                this.clientWorldEvent.onStart(LycanitesMobs.proxy.getClientPlayer());
        }
    }

    /**
     * Starts the provided World Event (provided by name) on the provided world.
     *  **/
    public void startWorldEvent(String mobEventName) {
        MobEventBase mobEvent;
        if(MobEventManager.instance.worldMobEvents.containsKey(mobEventName)) {
            mobEvent = MobEventManager.instance.worldMobEvents.get(mobEventName);
            if(!mobEvent.isEnabled()) {
                LycanitesMobs.printWarning("", "Tried to start a world event that was disabled with the name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
                return;
            }
        }
        else {
            LycanitesMobs.printWarning("", "Tried to start a world event with the invalid name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
            return;
        }

        this.startWorldEvent(mobEvent);
    }


    // ==================================================
    //                 Stop World Event
    // ==================================================
    /**
     * Stops the World Event.
     *  **/
    public void stopWorldEvent() {
        // Server Side:
        if(this.serverWorldEvent != null) {
            this.serverWorldEvent.onFinish();
            this.setWorldEventType("");
            this.serverWorldEvent = null;
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.clientWorldEvent != null) {
            if(LycanitesMobs.proxy.getClientPlayer() != null)
                this.clientWorldEvent.onFinish(LycanitesMobs.proxy.getClientPlayer());
            this.clientWorldEvent = null;
        }
    }


    // ==================================================
    //                 Start Mob Event
    // ==================================================
    /**
     * Starts a provided Mob Event (provided by instance) on the provided world.
     *  **/
    public void startMobEvent(MobEventBase mobEvent, int originX, int originY, int originZ) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null mob event.");
            return;
        }

        // Server Side:
        if(!this.world.isRemote) {
            MobEventServer mobEventServer = mobEvent.getServerEvent(this.world);
            this.serverMobEvents.add(mobEventServer);
            if(mobEventServer instanceof MobEventServerBoss) {
                MobEventServerBoss mobEventServerBoss = (MobEventServerBoss)mobEventServer;
                mobEventServerBoss.originX = originX;
                mobEventServerBoss.originY = originY;
                mobEventServerBoss.originZ = originZ;
            }
            mobEventServer.onStart();
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.world.isRemote) {
            boolean extended = false;
            if(this.clientMobEvents.get(mobEvent.name) != null)
                extended = this.clientMobEvents.get(mobEvent.name).mobEvent == mobEvent;
            if(!extended) {
                MobEventClient mobEventClient = mobEvent.getClientEvent(this.world);
                this.clientMobEvents.put(mobEvent.name, mobEventClient);
                mobEventClient.extended = extended;
                if (LycanitesMobs.proxy.getClientPlayer() != null)
                    mobEventClient.onStart(LycanitesMobs.proxy.getClientPlayer());
            }
        }
    }

    /**
     * Starts a provided World Event (provided by name) on the provided world.
     *  **/
    public void startMobEvent(String mobEventName, int originX, int originY, int originZ) {
        MobEventBase mobEvent;
        if(MobEventManager.instance.allMobEvents.containsKey(mobEventName)) {
            mobEvent = MobEventManager.instance.allMobEvents.get(mobEventName);
            if(!mobEvent.isEnabled()) {
                LycanitesMobs.printWarning("", "Tried to start a mob event that was disabled with the name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
                return;
            }
        }
        else {
            LycanitesMobs.printWarning("", "Tried to start a mob event with the invalid name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
            return;
        }

        this.startMobEvent(mobEvent, originX, originY, originZ);
    }

    /**
     * Returns the next available index for a world event. Server only.
     */
    public int getNextMobEventIndex() {
        return this.serverMobEvents.size() + 1;
    }


    // ==================================================
    //                 Stop World Event
    // ==================================================
    /**
     * Stops a Mob Event. (Server Side)
     *  **/
    public void stopMobEvent(MobEventServer mobEventServer) {
        // Server Side:
        if(this.serverMobEvents.contains(mobEventServer)) {
            mobEventServer.onFinish();
            this.serverMobEvents.remove(mobEventServer);
            this.updateAllClientsEvents();
        }
    }

    /**
     * Stops a Mob Event. (Client Side)
     *  **/
    public void stopMobEvent(String mobEventName) {
        // Client Side:
        if(this.clientMobEvents.get(mobEventName) != null) {
            if(LycanitesMobs.proxy.getClientPlayer() != null)
                this.clientMobEvents.get(mobEventName).onFinish(LycanitesMobs.proxy.getClientPlayer());
            this.clientMobEvents.put(mobEventName, null);
        }
    }


    // ==================================================
    //                  Update Clients
    // ==================================================
    /** Sends a packet to all clients updating their events for the provided world. **/
    public void updateAllClientsEvents() {
        MessageWorldEvent message = new MessageWorldEvent(this.getWorldEventType());
        LycanitesMobs.packetHandler.sendToDimension(message, this.world.provider.dimensionId);
        for(MobEventServer mobEventServer : this.serverMobEvents) {
            MessageMobEvent messageMobEvent = new MessageMobEvent(mobEventServer.mobEvent != null ? mobEventServer.mobEvent.name : "");
            LycanitesMobs.packetHandler.sendToDimension(messageMobEvent, this.world.provider.dimensionId);
        }
    }
	
	
	// ==================================================
    //                    Read From NBT
    // ==================================================
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("WorldEventStartTargetTime"))  {
			this.worldEventStartTargetTime = nbtTagCompound.getInteger("WorldEventStartTargetTime");
		}
        if(nbtTagCompound.hasKey("WorldEventLastStartedTime"))  {
            this.worldEventLastStartedTime = nbtTagCompound.getInteger("WorldEventLastStartedTime");
        }
		if(nbtTagCompound.hasKey("WorldEventType"))  {
			this.worldEventType = nbtTagCompound.getString("WorldEventType");
		}
		if(nbtTagCompound.hasKey("WorldEventCount"))  {
			this.worldEventCount = nbtTagCompound.getInteger("WorldEventCount");
		}
	}
	
	
	// ==================================================
    //                    Write To NBT
    // ==================================================
	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setLong("WorldEventStartTargetTime", this.worldEventStartTargetTime);
		nbtTagCompound.setLong("WorldEventLastStartedTime", this.worldEventLastStartedTime);
    	nbtTagCompound.setString("WorldEventType", this.worldEventType);
    	nbtTagCompound.setInteger("WorldEventCount", this.worldEventCount);
	}
	
}
