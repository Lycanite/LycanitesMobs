package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.item.ItemSummoningStaff;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemStaffBelph extends ItemSummoningStaff {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffBelph(int itemID) {
        super(itemID);
    	this.domain = DemonMobs.domain;
    	this.itemName = "BelphStaff";
    	this.textureName = "staffbelph";
        this.setUnlocalizedName(this.itemName);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getSummonCost(ItemStack itemStack) {
        return 2;
    }
    
    @Override
    public void summonCreatures(World world, EntityPlayer player, int summonAmount) {
    	super.summonCreatures(world, player, summonAmount);
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == ObjectManager.getItem("DoomfireCharge").itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
