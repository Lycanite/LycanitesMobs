package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.item.ItemScepter;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterHellfire extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterHellfire() {
        super();
    	this.group = DemonMobs.group;
    	this.itemName = "hellfirescepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
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
    		EntityProjectileBase projectile = new EntityHellfireball(world, entity);
    		projectile.setBaseDamage((int)(projectile.getDamage(null) * power * 2));
        	world.spawnEntityInWorld(projectile);
            this.playSound(itemStack, world, entity, power, projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("hellfirecharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
