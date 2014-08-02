package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffSavage extends ItemStaffSummoning {
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSavage() {
        super();
        this.itemName = "savagesummoningstaff";
        this.setup();
        this.textureName = "staffsavage";
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
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
    // ========== Summon Amount ==========
    public int getSummonAmount() {
    	return 2;
    }
    
    // ========== Minion Effects ==========
    public void applyMinionEffects(EntityCreatureBase minion) {
    	minion.setHealth(minion.getHealth() / 2);
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
