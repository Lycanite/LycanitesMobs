package lycanite.lycanitesmobs.api.item;

import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffStable extends ItemStaffSummoning {
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffStable() {
        super();
        this.itemName = "stablesummoningstaff";
        this.setup();
        this.textureName = "staffstable";
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add("\u00a7a" + "Consumes more Focus but");
    	par3List.add("\u00a7a" + "minions last longer.");
    	super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
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
    	return 2;
    }
    public float getSummonCostMod() {
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 120 * 20;
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
