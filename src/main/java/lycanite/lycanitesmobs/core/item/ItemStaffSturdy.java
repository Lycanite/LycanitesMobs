package lycanite.lycanitesmobs.core.item;

import lycanite.lycanitesmobs.core.entity.EntityPortal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffSturdy extends ItemStaffSummoning {
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSturdy() {
        super();
        this.itemName = "sturdysummoningstaff";
        this.setup();
        this.textureName = "staffsturdy";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 1000;
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 20;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCostBoost() {
    	return 2;
    }
    public float getSummonCostMod() {
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
    	if(repairStack.getItem() == Items.GOLD_INGOT) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
