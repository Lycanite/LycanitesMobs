package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.item.ItemScepter;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterVenomShot extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterVenomShot() {
        super();
    	this.group = SwampMobs.group;
    	this.itemName = "venomshotscepter";
        this.setup();
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 5;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	EntityVenomShot projectile = new EntityVenomShot(world, entity);
        	world.spawnEntityInWorld(projectile);
            this.playSound(itemStack, world, entity, 1, projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == Items.ROTTEN_FLESH || repairStack.getItem() == ObjectManager.getItem("poisongland")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
