package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.arcticmobs.entity.EntityTundra;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterTundra extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterTundra() {
        super();
    	this.group = ArcticMobs.instance.group;
    	this.itemName = "tundrascepter";
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
    		EntityProjectileBase projectile = new EntityTundra(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("tundracharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
