package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.network.MessageMobEvent;
import com.lycanitesmobs.core.network.MessageWorldEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

public class ExtendedWorld extends WorldSavedData {
	public static String EXT_PROP_NAME = "LycanitesMobs";
	public static Map<World, ExtendedWorld> loadedExtWorlds = new HashMap<>();

    /** The world INSTANCE to work with. **/
	public World world;
	public boolean initialized = false;
	public long lastSpawnerTime = 0;
	public long lastEventScheduleTime = 0;
	public long lastEventUpdateTime = 0;

    // Mob Events World Config:
    public boolean useTotalWorldTime = true;
	
	// Mob Events:
    public Map<String, MobEventPlayerServer> serverMobEventPlayers = new HashMap<>();
    public Map<String, MobEventPlayerClient> clientMobEventPlayers = new HashMap<>();
    public MobEventPlayerServer serverWorldEventPlayer = null;
    public MobEventPlayerClient clientWorldEventPlayer = null;
	private long worldEventStartTargetTime = 0;
    private long worldEventLastStartedTime = 0;
	private String worldEventName = "";
	private int worldEventCount = -1;

	// Dungeons:
	public Map<UUID, DungeonInstance> dungeons = new HashMap<>();

	
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

        if (world.MAX_ENTITY_RADIUS < 50)
            world.MAX_ENTITY_RADIUS = 50;
		WorldSavedData worldSavedData = world.getPerWorldStorage().getOrLoadData(ExtendedWorld.class, EXT_PROP_NAME);
		if(worldSavedData != null) {
			worldExt = (ExtendedWorld)worldSavedData;
			worldExt.world = world;
			worldExt.init();
		}
		else {
			worldExt = new ExtendedWorld(world);
			world.getPerWorldStorage().setData(EXT_PROP_NAME, worldExt);
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
		//this.loadConfig();
	}
	
	
	// ==================================================
    //                        Init
    // ==================================================
	public void init() {
		if(this.initialized) {
			return;
		}
		this.initialized = true;

		// Initial Tick Times:
        this.lastSpawnerTime = this.world.getTotalWorldTime() - 1;
		this.lastEventScheduleTime = this.world.getTotalWorldTime() - 1;
		this.lastEventUpdateTime = this.world.getTotalWorldTime() - 1;

		// Start Saved Events:
		if (!this.world.isRemote && !"".equals(this.getWorldEventName()) && this.serverWorldEventPlayer == null) {
			long savedLastStartedTime = this.getWorldEventLastStartedTime();
			this.startMobEvent(this.getWorldEventName(), null, new BlockPos(0, 0, 0), 1); // TODO Swap to read/write from NBT on Server Players.
			if (this.serverWorldEventPlayer != null) {
				this.serverWorldEventPlayer.changeStartedWorldTime(savedLastStartedTime);
			}
		}
	}


    // ==================================================
    //                     Load Config
    // ==================================================
    public void loadConfig() {
        ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        //this.mobEventsEnabled = config.getBool("World", this.getConfigEntryName("Mob Events Enabled"), mobEventsEnabled, "If false, all mob events will be completely disabled for this world.");

        //this.mobEventsRandom = config.getBool("World", this.getConfigEntryName("Random Mob Events"), mobEventsRandom, "If false, mob events will no longer occur randomly but can still occur via other means such as by schedule. Set this to true if you want both random and scheduled events to take place and also take a look at 'Lock Random Mob Events' if doing so.");
        //this.minEventsRandomDay = config.getInt("World", this.getConfigEntryName("Random Mob Events Day Minimum"), this.minEventsRandomDay, "If random events are enabled, they wont occur until this day is reached. Set to 0 to have random events enabled from the start of a world.");

        //this.minTicksUntilEvent = config.getInt("World", this.getConfigEntryName("Min Ticks Until Random Event"), this.minTicksUntilEvent, "Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
        //this.maxTicksUntilEvent = config.getInt("World", this.getConfigEntryName("Max Ticks Until Random Event"), this.maxTicksUntilEvent, "Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.");

        //this.configLoaded = true;
    }

    protected String getConfigEntryName(String name) {
        return name + " " + this.world.provider.getDimensionType().getName() + " (" + this.world.provider.getDimension() + ")";
    }
	
	
	// ==================================================
    //                    Get Properties
    // ==================================================
	public long getWorldEventStartTargetTime() {
		return this.worldEventStartTargetTime;
	}

    public long getWorldEventLastStartedTime() {
		return this.worldEventLastStartedTime;
	}

	public String getWorldEventName() {
		return this.worldEventName;
	}

	public MobEvent getWorldEvent() {
		if(this.getWorldEventName() == null || "".equals(this.getWorldEventName())) {
			return null;
		}
		return MobEventManager.getInstance().getMobEvent(this.getWorldEventName());
	}

	public int getWorldEventCount() {
		return this.worldEventCount;
	}
	
	
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

	public void setWorldEventName(String setString) {
		if(!this.worldEventName.equals(setString))
			this.markDirty();
		this.worldEventName = setString;
	}

	public void increaseMobEventCount() {
		this.worldEventCount++;
	}


    // ==================================================
    //                Random Event Delay
    // ==================================================
    /** Gets a random time until the next random event will start. **/
    public int getRandomEventDelay(Random random) {
		int min = Math.max(200, MobEventManager.getInstance().minTicksUntilEvent);
        int max = Math.max(200, MobEventManager.getInstance().maxTicksUntilEvent);
        if(max <= min)
            return min;

        return min + random.nextInt(max - min);
    }


    // ==================================================
    //                     World Event
    // ==================================================
    /**
     * Starts the provided Mob Event on the provided world.
     *  **/
    public void startWorldEvent(MobEvent mobEvent) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null world event, stopping any event instead.");
            this.stopWorldEvent();
            return;
        }

        // Server Side:
        if(!this.world.isRemote) {
			boolean extended = false;
			if(this.serverWorldEventPlayer != null) {
				extended = this.serverWorldEventPlayer.mobEvent == mobEvent;
			}
			if(!extended) {
				this.serverWorldEventPlayer = mobEvent.getServerEventPlayer(this.world);
			}
			this.serverWorldEventPlayer.extended = extended;

            this.setWorldEventName(mobEvent.name);
            this.increaseMobEventCount();
            this.setWorldEventStartTargetTime(0);
            this.setWorldEventLastStartedTime(this.world.getTotalWorldTime());
            this.serverWorldEventPlayer.onStart();
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.world.isRemote) {
            boolean extended = false;
            if(this.clientWorldEventPlayer != null) {
				extended = this.clientWorldEventPlayer.mobEvent == mobEvent;
			}
			if(!extended) {
				this.clientWorldEventPlayer = mobEvent.getClientEventPlayer(this.world);
			}
			this.clientWorldEventPlayer.extended = extended;
			if(LycanitesMobs.proxy.getClientPlayer() != null) {
				this.clientWorldEventPlayer.onStart(LycanitesMobs.proxy.getClientPlayer());
			}
        }
    }

    /**
     * Stops the World Event.
    **/
    public void stopWorldEvent() {
        // Server Side:
        if(this.serverWorldEventPlayer != null) {
            this.serverWorldEventPlayer.onFinish();
            this.setWorldEventName("");
            this.serverWorldEventPlayer = null;
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.clientWorldEventPlayer != null) {
            if(LycanitesMobs.proxy.getClientPlayer() != null)
                this.clientWorldEventPlayer.onFinish(LycanitesMobs.proxy.getClientPlayer());
            this.clientWorldEventPlayer = null;
        }
    }


    // ==================================================
    //                     Mob Events
    // ==================================================
    /**
     * Starts a provided Mob Event (provided by INSTANCE) on the provided world.
     *  **/
    public void startMobEvent(MobEvent mobEvent, EntityPlayer player, BlockPos pos, int level) {
        if(mobEvent == null) {
            LycanitesMobs.printWarning("", "Tried to start a null mob event.");
            return;
        }

        // Server Side:
        if(!this.world.isRemote) {
            MobEventPlayerServer mobEventPlayerServer = mobEvent.getServerEventPlayer(this.world);
            this.serverMobEventPlayers.put(mobEvent.name, mobEventPlayerServer);
			mobEventPlayerServer.player = player;
            mobEventPlayerServer.origin = pos;
            mobEventPlayerServer.level = level;
            mobEventPlayerServer.onStart();
            this.updateAllClientsEvents();
        }

        // Client Side:
        if(this.world.isRemote) {
			MobEventPlayerClient mobEventPlayerClient;
            if(this.clientMobEventPlayers.containsKey(mobEvent.name)) {
				mobEventPlayerClient = this.clientMobEventPlayers.get(mobEvent.name);
			}
			else {
				mobEventPlayerClient = mobEvent.getClientEventPlayer(this.world);
				this.clientMobEventPlayers.put(mobEvent.name, mobEventPlayerClient);
			}
			if(LycanitesMobs.proxy.getClientPlayer() != null) {
				mobEventPlayerClient.onStart(LycanitesMobs.proxy.getClientPlayer());
			}
        }
    }

    /**
     * Starts a provided Mob Event (provided by name) on the provided world.
     **/
    public MobEvent startMobEvent(String mobEventName, EntityPlayer player, BlockPos pos, int level) {
        MobEvent mobEvent;
        if(MobEventManager.getInstance().mobEvents.containsKey(mobEventName)) {
            mobEvent = MobEventManager.getInstance().mobEvents.get(mobEventName);
            if(!mobEvent.isEnabled()) {
                LycanitesMobs.printWarning("", "Tried to start a mob event that was disabled with the name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
                return null;
            }
        }
        else {
            LycanitesMobs.printWarning("", "Tried to start a mob event with the invalid name: '" + mobEventName + "' on " + (this.world.isRemote ? "Client" : "Server"));
            return null;
        }

        mobEvent.trigger(world, player, pos, level);
        return mobEvent;
    }

    /**
     * Stops a Mob Event.
     *  **/
    public void stopMobEvent(String mobEventName) {
		// Server Side:
		if(!this.world.isRemote) {
			if (this.serverMobEventPlayers.containsKey(mobEventName)) {
				this.serverMobEventPlayers.get(mobEventName).onFinish();
				this.serverMobEventPlayers.remove(mobEventName);
				this.updateAllClientsEvents();
			}
		}

        // Client Side:
		if(this.world.isRemote) {
			if (this.clientMobEventPlayers.containsKey(mobEventName)) {
				if(LycanitesMobs.proxy.getClientPlayer() != null) {
					this.clientMobEventPlayers.get(mobEventName).onFinish(LycanitesMobs.proxy.getClientPlayer());
				}
				this.clientMobEventPlayers.remove(mobEventName);
			}
		}
    }


	// ==================================================
	//            Get Active Mob Event Players
	// ==================================================
	/** Returns a Mob Event Server Player if an event by the provided event name is currently active, otherwise null. **/
	public MobEventPlayerServer getMobEventPlayerServer(String mobEventName) {
		if(mobEventName == null || "".equals(mobEventName)) {
			return null;
		}

		if(mobEventName.equals(this.getWorldEventName())) {
			return this.serverWorldEventPlayer;
		}

		if(this.serverMobEventPlayers.containsKey(mobEventName)) {
			return this.serverMobEventPlayers.get(mobEventName);
		}

		return null;
	}

	/** Returns a Mob Event Client Player if an event by the provided event name is currently active, otherwise null. **/
	public MobEventPlayerClient getMobEventPlayerClient(String mobEventName) {
		if(mobEventName == null || "".equals(mobEventName)) {
			return null;
		}

		if(mobEventName.equals(this.worldEventName)) {
			return this.clientWorldEventPlayer;
		}

		if(this.clientMobEventPlayers.containsKey(mobEventName)) {
			return this.clientMobEventPlayers.get(mobEventName);
		}

		return null;
	}


	// ==================================================
    //                  Update Clients
    // ==================================================
    /** Sends a packet to all clients updating their events for the provided world. **/
    public void updateAllClientsEvents() {
    	BlockPos pos = this.serverWorldEventPlayer != null ? this.serverWorldEventPlayer.origin : new BlockPos(0, 0, 0);
		int level = this.serverWorldEventPlayer != null ? this.serverWorldEventPlayer.level : 0;
        MessageWorldEvent message = new MessageWorldEvent(this.getWorldEventName(), pos, level);
        LycanitesMobs.packetHandler.sendToDimension(message, this.world.provider.getDimension());
        for(MobEventPlayerServer mobEventPlayerServer : this.serverMobEventPlayers.values()) {
            MessageMobEvent messageMobEvent = new MessageMobEvent(mobEventPlayerServer.mobEvent != null ? mobEventPlayerServer.mobEvent.name : "", mobEventPlayerServer.origin, mobEventPlayerServer.level);
            LycanitesMobs.packetHandler.sendToDimension(messageMobEvent, this.world.provider.getDimension());
        }
    }


	// ==================================================
	//                     Dungeons
	// ==================================================
	/**
	 * Adds a new Dungeon Instance to this world where it can be found for generation, etc. Gives a new UUID.
	 * @param dungeonInstance The Dungeon Instance to add.
	 * @param uuid The UUID to give to the new Dungeon Instance.
	 */
	public void addDungeonInstance(DungeonInstance dungeonInstance, UUID uuid) {
		dungeonInstance.uuid = uuid;
		this.dungeons.put(dungeonInstance.uuid, dungeonInstance);
		this.markDirty();
	}


	/**
	 * Returns all Dungeon Instances loaded for the provided world within range of the chunk position.
	 * @param chunkPos The chunk position to search around.
	 * @param range The range from the chunk position.
	 * @return A list of Dungeon Instances found.
	 */
	public List<DungeonInstance> getNearbyDungeonInstances(ChunkPos chunkPos, int range) {
		List<DungeonInstance> nearbyDungeons = new ArrayList<>();
		for(DungeonInstance dungeonInstance : this.dungeons.values()) {
			if(dungeonInstance.world == null) {
				dungeonInstance.init(this.world);
			}
			if(dungeonInstance.isChunkPosWithin(chunkPos, range)) {
				nearbyDungeons.add(dungeonInstance);
			}
		}
		return nearbyDungeons;
	}
	
	
	// ==================================================
    //                    Read From NBT
    // ==================================================
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		// Events:
		if(nbtTagCompound.hasKey("WorldEventStartTargetTime"))  {
			this.worldEventStartTargetTime = nbtTagCompound.getInteger("WorldEventStartTargetTime");
		}
        if(nbtTagCompound.hasKey("WorldEventLastStartedTime"))  {
            this.worldEventLastStartedTime = nbtTagCompound.getInteger("WorldEventLastStartedTime");
        }
		if(nbtTagCompound.hasKey("WorldEventName"))  {
			this.worldEventName = nbtTagCompound.getString("WorldEventName");
		}
		if(nbtTagCompound.hasKey("WorldEventCount"))  {
			this.worldEventCount = nbtTagCompound.getInteger("WorldEventCount");
		}
		// TODO Load all active mob events, not just the world event.

		// Dungeons:
		if(nbtTagCompound.hasKey("Dungeons"))  {
			NBTTagList nbtDungeonList = nbtTagCompound.getTagList("Dungeons", 10);
			for(int i = 0; i < nbtDungeonList.tagCount(); i++) {
				try {
					NBTTagCompound dungeonNBT = nbtDungeonList.getCompoundTagAt(i);
					DungeonInstance dungeonInstance = new DungeonInstance();
					dungeonInstance.readFromNBT(dungeonNBT);
					if(dungeonInstance.uuid != null && !this.dungeons.containsKey(dungeonInstance.uuid)) {
						this.dungeons.put(dungeonInstance.uuid, dungeonInstance);
					}
				}
				catch(Exception e) {
					LycanitesMobs.printWarning("Dungeon", "An exception occurred when loading a dungeon from NBT.");
				}
			}
		}
	}
	
	
	// ==================================================
    //                    Write To NBT
    // ==================================================
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
    	// Events:
		nbtTagCompound.setLong("WorldEventStartTargetTime", this.worldEventStartTargetTime);
		nbtTagCompound.setLong("WorldEventLastStartedTime", this.worldEventLastStartedTime);
    	nbtTagCompound.setString("WorldEventName", this.worldEventName);
    	nbtTagCompound.setInteger("WorldEventCount", this.worldEventCount);
    	// TODO Save all active mob events, not just the world event.

		// Dungeons:
		NBTTagList nbtDungeonList = new NBTTagList();
		for(DungeonInstance dungeonInstance : this.dungeons.values()) {
			NBTTagCompound dungeonNBT = new NBTTagCompound();
			dungeonNBT = dungeonInstance.writeToNBT(dungeonNBT);
			if(dungeonNBT != null) {
				nbtDungeonList.appendTag(dungeonNBT);
			}
		}
		nbtTagCompound.setTag("Dungeons", nbtDungeonList);

        return nbtTagCompound;
	}
	
}
