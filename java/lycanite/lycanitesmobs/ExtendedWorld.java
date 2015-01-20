package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lycanite.lycanitesmobs.api.ValuePair;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventClient;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.mobevent.MobEventServer;
import lycanite.lycanitesmobs.api.network.MessageMobEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServerMulti;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.math.NumberUtils;

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
    public int minTicksUntilEvent = 10 * 60 * 20;
    public int maxTicksUntilEvent = 30 * 60 * 20;
    public boolean mobEventsEnabled = true;
    public boolean mobEventsRandom = true;
    public String mobEventsSchedule = "";
    public boolean eventScheduleLoaded = false;
    public boolean mobEventsLocked = false;
    public boolean mobEventsLockedOnlyOnSchedule = true;
    public int minEventsRandomDay = 0;
	
	// Mob Events:
    public Map<ValuePair<Integer, Integer>, MobEventBase> eventSchedule;
    public MobEventServer serverMobEvent = null;
    public MobEventClient clientMobEvent = null;
	private long mobEventStartTargetTime = 0;
    private long mobEventLastStartedTime = 0;
	private String mobEventType = "";
	private int mobEventCount = -1;
	
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
		if(!this.world.isRemote && !"".equals(this.getMobEventType()) && this.serverMobEvent == null) {
            long savedLastStartedTime = this.getMobEventLastStartedTime();
			this.startMobEvent(this.getMobEventType());
			if(this.serverMobEvent != null) {
                this.serverMobEvent.changeStartedWorldTime(savedLastStartedTime);
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
	public long getMobEventStartTargetTime() { return this.mobEventStartTargetTime; }
    public long getMobEventLastStartedTime() { return this.mobEventLastStartedTime; }
	public String getMobEventType() { return this.mobEventType; }
	public int getMobEventCount() { return this.mobEventCount; }
	
	
	// ==================================================
    //                    Set Properties
    // ==================================================
	public void setMobEventStartTargetTime(long setLong) {
		if(this.mobEventStartTargetTime != setLong)
			this.markDirty();
		this.mobEventStartTargetTime = setLong;
        if(setLong > 0)
            LycanitesMobs.printDebug("MobEvents", "Next random mob will start after " + ((this.mobEventStartTargetTime - this.world.getTotalWorldTime()) / 20) + "secs.");
	}
    public void setMobEventLastStartedTime(long setLong) {
        if(this.mobEventLastStartedTime != setLong)
            this.markDirty();
        this.mobEventLastStartedTime = setLong;
    }
	public void setMobEventType(String setString) {
		if(!this.mobEventType.equals(setString))
			this.markDirty();
		this.mobEventType = setString;
	}
	public void increaseMobEventCount() {
		this.mobEventCount++;
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
    //                 Stop Mob Event
    // ==================================================
    /**
     * Stops the Mob Event.
     *  **/
    public void stopMobEvent() {
        // Server Side:
        if(this.serverMobEvent != null) {
            this.serverMobEvent.onFinish();
            this.setMobEventType("");
            this.serverMobEvent = null;
            this.updateAllClientsEvents();
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
    public void startMobEvent(MobEventBase mobEvent) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null mob event, stopping any event instead.");
            this.stopMobEvent();
        }

        // Server Side:
        if(!this.world.isRemote) {
            this.serverMobEvent = mobEvent.getServerEvent(this.world);
            this.setMobEventType(mobEvent.name);
            this.increaseMobEventCount();
            this.setMobEventStartTargetTime(0);
            this.setMobEventLastStartedTime(this.world.getTotalWorldTime());
            this.serverMobEvent.onStart();
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.world.isRemote) {
            boolean extended = false;
            if(this.clientMobEvent != null)
                extended = this.clientMobEvent.mobEvent == mobEvent;
            this.clientMobEvent = mobEvent.getClientEvent(this.world);
            this.clientMobEvent.extended = extended;
            if(LycanitesMobs.proxy.getClientPlayer() != null)
                this.clientMobEvent.onStart(LycanitesMobs.proxy.getClientPlayer());
        }
    }

    /**
     * Starts the provided Mob Event (provided by name) on the provided world.
     *  **/
    public void startMobEvent(String mobEventName) {
        MobEventBase mobEvent;
        if(MobEventManager.instance.worldMobEvents.containsKey(mobEventName)) {
            mobEvent = MobEventManager.instance.worldMobEvents.get(mobEventName);
            if(!mobEvent.isEnabled())
                mobEvent = null;
        }
        else {
            LycanitesMobs.printWarning("", "Tried to start an event with the invalid name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
            return;
        }

        this.startMobEvent(mobEvent);
    }


    // ==================================================
    //                  Update Clients
    // ==================================================
    /** Sends a packet to all clients updating their events for the provided world. **/
    public void updateAllClientsEvents() {
        MessageMobEvent message = new MessageMobEvent(this.getMobEventType());
        LycanitesMobs.packetHandler.sendToDimension(message, this.world.provider.dimensionId);

    }
	
	
	// ==================================================
    //                    Read From NBT
    // ==================================================
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("MobEventStartTargetTime"))  {
			this.mobEventStartTargetTime = nbtTagCompound.getInteger("MobEventStartTargetTime");
		}
        if(nbtTagCompound.hasKey("MobEventLastStartedTime"))  {
            this.mobEventLastStartedTime = nbtTagCompound.getInteger("MobEventLastStartedTime");
        }
		if(nbtTagCompound.hasKey("MobEventType"))  {
			this.mobEventType = nbtTagCompound.getString("MobEventType");
		}
		if(nbtTagCompound.hasKey("MobEventCount"))  {
			this.mobEventCount = nbtTagCompound.getInteger("MobEventCount");
		}
	}
	
	
	// ==================================================
    //                    Write To NBT
    // ==================================================
	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setLong("MobEventStartTargetTime", this.mobEventStartTargetTime);
		nbtTagCompound.setLong("MobEventLastStartedTime", this.mobEventLastStartedTime);
    	nbtTagCompound.setString("MobEventType", this.mobEventType);
    	nbtTagCompound.setInteger("MobEventCount", this.mobEventCount);
	}
	
}
