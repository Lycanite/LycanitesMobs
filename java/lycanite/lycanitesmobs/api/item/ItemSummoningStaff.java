package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSummoningStaff extends ItemScepter {
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSummoningStaff(int itemID) {
        super(itemID);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 250;
    }
    
    @Override
    public void damageItemRapid(ItemStack itemStack, EntityPlayer player) {
        return;
    }
    
    public void damageItemCharged(ItemStack itemStack, EntityPlayer player, float power) {
    	if(this.portalEntity != null) {
    		itemStack.damageItem((int)(5 * this.portalEntity.summonAmount), player);
    	}
    }
    
    // ========== Charge Time ==========
    @Override
    public int getChargeTime(ItemStack itemStack) {
        return this.getRapidTime(itemStack);
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 40;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCost() {
    	return 1;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    // ========== Start ==========
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
	    	this.portalEntity = new EntityPortal(world, player, this);
	    	this.portalEntity.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
	    	world.spawnEntityInWorld(this.portalEntity);
    	}
        return super.onItemRightClick(itemStack, world, player);
    }
    
    // ========== Rapid ==========
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	return false;
    }
    
    // ========== Charged ==========
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
    	if(this.portalEntity != null) {
			return this.portalEntity.summonCreatures();
		}
		return false;
    }

    // ========== Get Summon Entity ==========
    public EntityCreatureTameable getSummonEntity(World world) {
    	return null;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == ObjectManager.getItem("HellfireCharge").itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
