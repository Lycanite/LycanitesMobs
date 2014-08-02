package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffBlood extends ItemStaffSummoning {
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffBlood() {
        super();
        this.itemName = "bloodsummoningstaff";
        this.setup();
        this.textureName = "staffblood";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 250;
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 40;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCostBoost() {
    	return 0;
    }
    public float getSummonCostMod() {
    	return 0.5F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
    // ========== Additional Costs ==========
    public boolean getAdditionalCosts(EntityPlayer player) {
    	if(player.getHealth() < 7)
    		return false;
    	if(player.getFoodStats().getFoodLevel() < 6)
    		return false;
    	player.setHealth(player.getHealth() - 6);
    	player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 6);
    	return true;
    }
    
	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == Items.gold_ingot) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
