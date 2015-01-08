package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class ExtendedWorld extends WorldSavedData {
	public static String EXT_PROP_NAME = "LycanitesMobs";
	public static Map<World, ExtendedWorld> loadedExtWorlds = new HashMap<World, ExtendedWorld>();
	
	public World world;
    public long lastEventUpdateTime = 0;
    /** The last minute used when checking for scheduled events. **/
    public int lastEventScheduleMinute = -1;
	
	// Mob Events:
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
		
		// Already Loaded:
		if(loadedExtWorlds.containsKey(world))
			return loadedExtWorlds.get(world);
		
		WorldSavedData worldSavedData = world.loadItemData(ExtendedWorld.class, EXT_PROP_NAME);
		ExtendedWorld worldExt;
		if(worldSavedData != null) {
			worldExt = (ExtendedWorld)worldSavedData;
			worldExt.world = world;
			worldExt.init();
		}
		else {
			worldExt = new ExtendedWorld(world);
			world.setItemData(EXT_PROP_NAME, worldExt);
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
        this.lastEventUpdateTime = world.getTotalWorldTime() - 1;
        int currentTotalMinutes = (int)Math.floor((MobEventManager.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 1000D);
        this.lastEventScheduleMinute = currentTotalMinutes % 24;

		// Start Saved Event:
		if(!this.world.isRemote && !"".equals(this.getMobEventType()) && MobEventManager.instance.serverMobEvent == null) {
            long savedLastStartedTime = this.getMobEventLastStartedTime();
			MobEventManager.instance.startMobEvent(this.getMobEventType(), this.world);
			if(MobEventManager.instance.serverMobEvent != null) {
                MobEventManager.instance.serverMobEvent.changeStartedWorldTime(savedLastStartedTime);
            }
		}
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
        LycanitesMobs.printDebug("", "[MobEvent] Next random mob will start after " + ((this.mobEventStartTargetTime - this.world.getTotalWorldTime()) / 20) + "secs.");
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
