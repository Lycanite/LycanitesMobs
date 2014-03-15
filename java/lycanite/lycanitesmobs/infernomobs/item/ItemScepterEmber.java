package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterEmber extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterEmber(int itemID) {
        super(itemID);
    	this.domain = InfernoMobs.domain;
    	this.itemName = "EmberScepter";
    	this.textureName = "scepterember";
        this.setUnlocalizedName(this.itemName);
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
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	EntityEmber projectile = new EntityEmber(world, player);
        	world.spawnEntityInWorld(projectile);
            world.playSoundAtEntity(player, ((EntityProjectileBase)projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == ObjectManager.getItem("EmberCharge").itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
