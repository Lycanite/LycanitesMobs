package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.ValuePair;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.network.MessageMobEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.apache.commons.lang3.math.NumberUtils;


public class MobEventManager {
	// Global:
    public static MobEventManager instance;
    
    // Mob Events:
    public Map<String, MobEventBase> worldMobEvents = new HashMap<String, MobEventBase>();
    public Map<String, Map<String, MobEventBase>> worldMobEventSets = new HashMap<String, Map<String, MobEventBase>>();

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
		ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        config.setCategoryComment("Global", "These are various settings that apply to all events.");
        config.setCategoryComment("World", "These are various settings that apply to events on a per world basis. If your required world doesn't have its config values generated yet, you can generate them by entering the world in gae at least once.");

        baseRate = config.getInt("Global", "Base Spawn Rate", baseRate, "Sets the base interval in ticks (20 ticks = 1 second) between each mob spawn, this is multiplied by 1.5 on easy and 0.5 on hard.");
		baseRange = config.getInt("Global", "Base Spawn Range", baseRange, "Sets the base range in blocks from each player/area that event mobs will spawn.");

        config.setCategoryComment("Events Enabled", "Here each event can be turned on or off (true or false).");
        config.setCategoryComment("Events Mob Durations", "Here you can set the duration (in ticks where 20 ticks = 1 second) of each event.");
        config.setCategoryComment("Events Forced Spawning", "Sets which events force their mobs to spawn, forced spawns will ignore other mods that interfere with mob spawning.");
        config.setCategoryComment("Events Forced No Despawning", "Sets which events force their spawned mobs to not despawn naturally (like most vanilla monsters do). However, mobs spawned by events will always only last 10 minutes and will then be forcefully despawned unless they are tamed by players, given a name tag, etc.");
        config.setCategoryComment("Event Day Minimums", "The minimum day before each event can occur randomly. For example if Shadow Games is set to 10 then it wont ever occured as a random event until day 10. Note: If Schedules and Locked Random events are active, the random event will not occur until both the Minimum Event day set here and first Schedule is met (by default schedules and event locks aren't used).");
        config.setCategoryComment("Event Dimensions", "Sets which dimensions (by ID) that this event WILL NOT occur in. However if 'Spawn Dimensions Whitelist Mode' is set to true, it will instead set which dimensions that this event WILL ONLY occur in. Multiple entries should be comma separated.");
    }


    // ==================================================
    //                 Add Mob Event
    // ==================================================
    /**
     * Adds the provided World Mob Event.
     *  **/
    public void addWorldEvent(MobEventBase mobEvent, String set) {
        if(mobEvent != null && mobEvent.hasSpawners()) {
        	mobEvent.loadFromConfig();
            if(!this.worldMobEventSets.containsKey(set))
                this.worldMobEventSets.put(set, new HashMap<String, MobEventBase>());
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

        /*int currentDay = (int)Math.floor((MobEventManager.useTotalWorldTime ? world.getWorldTime() : world.getWorldTime()) / 24000D);
        int currentMin = (int)((long)Math.floor((MobEventManager.useTotalWorldTime ? world.getWorldTime() : world.getWorldTime()) / 1200D) % 20);
        int currentSec = (int)((long)Math.floor((MobEventManager.useTotalWorldTime ? world.getWorldTime() : world.getWorldTime()) / 20D) % 60);
        LycanitesMobs.printDebug("", "Current Time: Day " + currentDay + " " + currentMin + ":" + currentSec);//XXX*/

        // Check If Events Are Completely Disabled:
        if(!worldExt.mobEventsEnabled || world.difficultySetting == EnumDifficulty.PEACEFUL) {
            if(worldExt.serverMobEvent != null)
                worldExt.stopMobEvent();
            return;
        }

        // Only Tick On World Time Ticks:
        if(worldExt.lastEventUpdateTime == world.getTotalWorldTime()) return;
        worldExt.lastEventUpdateTime = world.getTotalWorldTime();
		
		// Only Run If Players Are Present:
		if(world.playerEntities.size() < 1) {
			return;
		}

        // Scheduled Events:
        if(!worldExt.eventScheduleLoaded)
            worldExt.loadEventSchedule(worldExt.mobEventsSchedule);
        if(worldExt.eventSchedule != null && worldExt.eventSchedule.size() > 0) {
            MobEventBase newEvent = worldExt.getScheduledWorldMobEvent();
            if(newEvent != null) {
                worldExt.startMobEvent(newEvent);
            }
        }

        // Update Active Event If Present and Return:
        if(worldExt.serverMobEvent != null) {
            worldExt.serverMobEvent.onUpdate();
            return;
        }

        // Random Events:
        if(!worldExt.mobEventsRandom) return;
        if(worldExt.minEventsRandomDay > 0 && Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000D) < worldExt.minEventsRandomDay) return;
        if(worldExt.getMobEventStartTargetTime() <= 0 || worldExt.getMobEventStartTargetTime() > world.getTotalWorldTime() + worldExt.maxTicksUntilEvent) {
            worldExt.setMobEventStartTargetTime(world.getTotalWorldTime() + worldExt.getRandomEventDelay(world.rand));
        }
        if(world.getTotalWorldTime() >= worldExt.getMobEventStartTargetTime()) {
			MobEventBase newEvent = this.getRandomWorldMobEvent(world, worldExt);
			if(newEvent != null) {
                worldExt.startMobEvent(newEvent);
			}
		}
    }
	
	
    // ==================================================
    //                 Client Update Event
    // ==================================================
	/** Updates the client side mob event instance if present in the player's current world. **/
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent event) {
		if(LycanitesMobs.proxy.getClientPlayer() == null)
			return;

        World world = LycanitesMobs.proxy.getClientPlayer().worldObj;
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(!world.isRemote || worldExt == null) return;

		// Update Active Event and Return:
		if(worldExt.clientMobEvent != null) {
			worldExt.clientMobEvent.onUpdate();
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
