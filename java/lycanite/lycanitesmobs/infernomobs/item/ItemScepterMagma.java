package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.infernomobs.entity.EntityMagma;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterMagma extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterMagma() {
        super();
    	this.domain = InfernoMobs.domain;
    	this.itemName = "MagmaScepter";
        this.setUnlocalizedName(this.itemName);
        this.textureName = "sceptermagma";
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
        return 30;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
    	if(!world.isRemote) {
    		EntityProjectileBase projectile = new EntityMagma(world, player);
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
        if(repairStack.getItem() == ObjectManager.getItem("MagmaCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
