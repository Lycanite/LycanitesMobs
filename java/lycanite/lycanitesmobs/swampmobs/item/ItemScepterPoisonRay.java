package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterPoisonRay extends ItemScepter {
	private EntityProjectileLaser projectileTarget;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterPoisonRay(int itemID) {
        super(itemID);
    	this.domain = SwampMobs.domain;
    	this.itemName = "PoisonRayScepter";
        this.setUnlocalizedName(this.itemName);
        this.textureName = "scepterpoisonray";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Start ==========
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	this.projectileTarget = null;
        return super.onItemRightClick(itemStack, world, player);
    }
    
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack par1ItemStack) {
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
    		if(this.projectileTarget != null && this.projectileTarget.isEntityAlive()) {
    			projectileTarget.setTime(20);
    		}
    		else {
    			this.projectileTarget = new EntityPoisonRay(world, player, 20, 10);
    			world.spawnEntityInWorld(this.projectileTarget);
            	world.playSoundAtEntity(player, ((EntityProjectileBase)this.projectileTarget).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    		}
    	}
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == Item.fermentedSpiderEye.itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
