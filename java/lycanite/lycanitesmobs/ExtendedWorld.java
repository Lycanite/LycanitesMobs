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
	
	// Mob Events:
	private int mobEventTime = 0;
	private int mobEventTarget = 0;
	private int mobEventActiveTime = 0;
	private String mobEventType = "";
	
	// ==================================================
    //                   Get for World
    // ==================================================
	public static ExtendedWorld getForWorld(World world) {
		if(world == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedWorld from a null World.");
			return null;
		}
		
		// ALready Loaded:
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
		// Start Saved Event:
		if(!this.world.isRemote && !"".equals(this.mobEventType) && MobEventManager.instance.activeMobEvent == null) {
			MobEventManager.instance.startMobEvent(this.mobEventType, this.world, this);
			if(MobEventManager.instance.activeMobEvent != null)
				MobEventManager.instance.activeMobEvent.serverTicks = this.mobEventActiveTime;
		}
	}
	
	
	// ==================================================
    //                    Get Properties
    // ==================================================
	public int getMobEventTime() { return this.mobEventTime; }
	public int getMobEventTarget() { return this.mobEventTarget; }
	public int getMobEventActiveTime() { return this.mobEventActiveTime; }
	public String getMobEventType() { return this.mobEventType; }
	
	
	// ==================================================
    //                    Set Properties
    // ==================================================
	public void setMobEventTime(int setInt) {
		if(this.mobEventTime != setInt)
			this.markDirty();
		this.mobEventTime = setInt;
	}
	public void setMobEventTarget(int setInt) {
		if(this.mobEventTarget != setInt)
			this.markDirty();
		this.mobEventTarget = setInt;
	}
	public void setMobEventActiveTime(int setInt) {
		if(this.mobEventActiveTime != setInt)
			this.markDirty();
		this.mobEventActiveTime = setInt;
	}
	public void setMobEventType(String setString) {
		if(!this.mobEventType.equals(setString))
			this.markDirty();
		this.mobEventType = setString;
	}
	
	
	// ==================================================
    //                    Read From NBT
    // ==================================================
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("MobEventTime"))  {
			this.mobEventTime = nbtTagCompound.getInteger("MobEventTime");
		}
		if(nbtTagCompound.hasKey("MobEventTarget"))  {
			this.mobEventTarget = nbtTagCompound.getInteger("MobEventTarget");
		}
		if(nbtTagCompound.hasKey("MobEventActiveTime"))  {
			this.mobEventActiveTime = nbtTagCompound.getInteger("MobEventActiveTime");
		}
		if(nbtTagCompound.hasKey("MobEventType"))  {
			this.mobEventType = nbtTagCompound.getString("MobEventType");
		}
	}
	
	
	// ==================================================
    //                    Write To NBT
    // ==================================================
	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setInteger("MobEventTime", this.mobEventTime);
		nbtTagCompound.setInteger("MobEventTarget", this.mobEventTarget);
		nbtTagCompound.setInteger("MobEventActiveTime", this.mobEventActiveTime);
    	nbtTagCompound.setString("MobEventType", this.mobEventType);
	}
	
}
