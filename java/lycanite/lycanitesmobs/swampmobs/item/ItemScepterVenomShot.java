package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterVenomShot extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterVenomShot(int itemID) {
        super(itemID);
    	this.domain = SwampMobs.domain;
    	this.itemName = "VenomShotScepter";
    	this.textureName = "sceptervenomshot";
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
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	EntityVenomShot projectile = new EntityVenomShot(world, player);
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
        if(repairStack.itemID == Item.rottenFlesh.itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
