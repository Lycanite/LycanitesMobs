package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityFrostbolt;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterFrostbolt extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterFrostbolt() {
        super();
    	this.group = ArcticMobs.group;
    	this.itemName = "frostboltscepter";
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
        	EntityFrostbolt projectile = new EntityFrostbolt(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("FrostboltCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
