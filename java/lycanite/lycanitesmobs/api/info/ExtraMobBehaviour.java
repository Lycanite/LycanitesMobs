package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.nbt.NBTTagCompound;

public class ExtraMobBehaviour {
	// ========== Mob ==========
	/** The instance of the mob this extra behaviour belongs to. **/
	public EntityCreatureBase host;
	
	// ========== Stats ==========
	public double multiplierDefense = 1.0D;
	public double multiplierSpeed = 1.0D;
	public double multiplierDamage = 1.0D;
	public double multiplierHaste = 1.0D;
	public double multiplierEffect = 1.0D;
	
	public int boostDefense = 0;
	public int boostSpeed = 0;
	public int boostDamage = 0;
	public int boostHaste = 0;
	public int boostEffect = 0;
	
	// ========== Overrides ==========
	public boolean flightOverride = false;
	public boolean swimmingOverride = false;
	public boolean waterBreathingOverride = false;
	public boolean fireImmunityOverride = false;
	public boolean stealthOverride = false;
	public boolean itemPickupOverride = false;
	public int inventorySizeOverride = 0;
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public ExtraMobBehaviour(EntityCreatureBase host) {
		this.host = host;
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Called from this host passing a compound storing all the extra behaviour options. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	// Stats:
    	if(nbtTagCompound.hasKey("MultiplierDefense")) {
    		this.multiplierDefense = nbtTagCompound.getDouble("MultiplierDefense");
    	}
    	if(nbtTagCompound.hasKey("MultiplierSpeed")) {
    		this.multiplierSpeed = nbtTagCompound.getDouble("MultiplierSpeed");
    	}
    	if(nbtTagCompound.hasKey("MultiplierDamage")) {
    		this.multiplierDamage = nbtTagCompound.getDouble("MultiplierDamage");
    	}
    	if(nbtTagCompound.hasKey("MultiplierHaste")) {
    		this.multiplierHaste = nbtTagCompound.getDouble("MultiplierHaste");
    	}
    	if(nbtTagCompound.hasKey("MultiplierEffect")) {
    		this.multiplierEffect = nbtTagCompound.getDouble("MultiplierEffect");
    	}

    	// Overrides:
    	if(nbtTagCompound.hasKey("FlightOverride")) {
    		this.flightOverride = nbtTagCompound.getBoolean("FlightOverride");
    	}
    	if(nbtTagCompound.hasKey("SwimmingOverride")) {
    		this.swimmingOverride = nbtTagCompound.getBoolean("SwimmingOverride");
    	}
    	if(nbtTagCompound.hasKey("WaterBreathingOverride")) {
    		this.waterBreathingOverride = nbtTagCompound.getBoolean("WaterBreathingOverride");
    	}
    	if(nbtTagCompound.hasKey("FireImmunityOverride")) {
    		this.fireImmunityOverride = nbtTagCompound.getBoolean("FireImmunityOverride");
    	}
    	if(nbtTagCompound.hasKey("StealthOverride")) {
    		this.stealthOverride = nbtTagCompound.getBoolean("StealthOverride");
    	}
    	if(nbtTagCompound.hasKey("ItemPickupOverride")) {
    		this.itemPickupOverride = nbtTagCompound.getBoolean("ItemPickupOverride");
    	}
    	if(nbtTagCompound.hasKey("InventorySizeOverride")) {
    		this.inventorySizeOverride = nbtTagCompound.getInteger("InventorySizeOverride");
    	}
    }
    
    // ========== Write ==========
    /** Called from this host passing a compound writing all the extra behaviour options. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	// Stats:
    	nbtTagCompound.setDouble("MultiplierDefense", this.multiplierDefense);
    	nbtTagCompound.setDouble("MultiplierSpeed", this.multiplierSpeed);
    	nbtTagCompound.setDouble("MultiplierDamage", this.multiplierDamage);
    	nbtTagCompound.setDouble("MultiplierHaste", this.multiplierHaste);
    	nbtTagCompound.setDouble("MultiplierEffect", this.multiplierEffect);

    	// Overrides:
    	nbtTagCompound.setBoolean("FlightOverride", this.flightOverride);
    	nbtTagCompound.setBoolean("SwimmingOverride", this.swimmingOverride);
    	nbtTagCompound.setBoolean("WaterBreathingOverride", this.waterBreathingOverride);
    	nbtTagCompound.setBoolean("FireImmunityOverride", this.fireImmunityOverride);
    	nbtTagCompound.setBoolean("StealthOverride", this.stealthOverride);
    	nbtTagCompound.setBoolean("ItemPickupOverride", this.itemPickupOverride);
    	nbtTagCompound.setInteger("InventorySizeOverride", this.inventorySizeOverride);
    }
}
