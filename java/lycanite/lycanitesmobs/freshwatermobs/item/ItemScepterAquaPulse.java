package lycanite.lycanitesmobs.freshwatermobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.item.ItemScepter;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityAquaPulse;
import net.minecraft.entity.EntityLivingBase;
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
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	if(!world.isRemote) {
    		EntityProjectileBase projectile = new EntityAquaPulse(world, entity);
    		projectile.setBaseDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntity(projectile);
            this.playSound(itemStack, world, entity, power, projectile);
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
