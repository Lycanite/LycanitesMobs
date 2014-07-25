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
    public ItemScepterEmber() {
        super();
    	this.domain = InfernoMobs.domain;
    	this.itemName = "emberscepter";
        this.setup();
        this.textureName = "scepterember";
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
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX + 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX - 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ + 1.0D);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ - 1.0D);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY + 1.0D, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityEmber(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY - 1.0D, projectile.posZ);
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
        if(repairStack.getItem() == ObjectManager.getItem("EmberCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
