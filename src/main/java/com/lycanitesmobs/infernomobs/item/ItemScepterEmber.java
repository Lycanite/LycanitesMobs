package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterEmber extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterEmber() {
        super();
    	this.group = InfernoMobs.instance.group;
    	this.itemName = "emberscepter";
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
        return 3;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	EntityEmber projectile = new EntityEmber(world, entity);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX + 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX - 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ + 1.0D);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ - 1.0D);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY + 1.0D, projectile.posZ);
        	world.spawnEntity(projectile);
        	
        	projectile = new EntityEmber(world, entity);
        	projectile.setPosition(projectile.posX, projectile.posY - 1.0D, projectile.posZ);
        	world.spawnEntity(projectile);

            this.playSound(itemStack, world, entity, 1, projectile);
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
