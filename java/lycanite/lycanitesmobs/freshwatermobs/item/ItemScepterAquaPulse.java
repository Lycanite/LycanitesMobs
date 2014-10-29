package lycanite.lycanitesmobs.freshwatermobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityAquaPulse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterAquaPulse extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterAquaPulse() {
        super();
    	this.group = FreshwaterMobs.group;
    	this.itemName = "aquapulsescepter";
        this.setup();
        this.textureName = "scepteraquapulse";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 80;
    }
    
    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return 15;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
    	if(!world.isRemote) {
    		EntityProjectileBase projectile = new EntityAquaPulse(world, player);
    		projectile.setBaseDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntityInWorld(projectile);
            world.playSoundAtEntity(player, projectile.getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("AquaPulseCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
