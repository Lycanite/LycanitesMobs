package lycanite.lycanitesmobs.mountainmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterArcaneLaserStorm extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterArcaneLaserStorm() {
        super();
    	this.group = MountainMobs.group;
    	this.itemName = "arcanelaserstormscepter";
        this.setup();
        this.textureName = "scepterarcanelaserstorm";
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
        return 30;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
    	if(!world.isRemote) {
    		EntityProjectileBase projectile = new EntityArcaneLaserStorm(world, player);
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
        if(repairStack.getItem() == ObjectManager.getItem("arcanelaserstormcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
