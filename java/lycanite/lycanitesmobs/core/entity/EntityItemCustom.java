package lycanite.lycanitesmobs.core.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityItemCustom extends EntityItem {
	
	// ==================================================
   	//                     Constructor
   	// ==================================================
	public EntityItemCustom(World world) {
		super(world);
	}
	
	public EntityItemCustom(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemCustom(World world, double x, double y, double z, ItemStack itemStack) {
        super(world, x, y, z, itemStack);
    }
    

	// ==================================================
   	//                   Taking Damage
   	// ==================================================
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
    	if(this.isImmuneToFire()) {
    		if(damageSource.isFireDamage() || "inFire".equalsIgnoreCase(damageSource.damageType)) {
    			return false;
    		}
    	}
        return super.attackEntityFrom(damageSource, damageAmount);
    }
    

	// ==================================================
   	//                    Immunities
   	// ==================================================
    public void setCanBurn(boolean canBurn) {
    	this.isImmuneToFire = !canBurn;
    	this.setFlag(0, false);
    }
    
    
    // ==================================================
   	//                  Network Flags
   	// ==================================================
    protected void setFlag(int flagID, boolean value) {
    	if(flagID == 0 && this.isImmuneToFire())
    		value = false;
        super.setFlag(flagID, value);
    }
}
