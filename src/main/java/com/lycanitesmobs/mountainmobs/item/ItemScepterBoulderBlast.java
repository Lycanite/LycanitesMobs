package com.lycanitesmobs.mountainmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.mountainmobs.MountainMobs;
import com.lycanitesmobs.mountainmobs.entity.EntityBoulderBlast;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterBoulderBlast extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterBoulderBlast() {
        super();
    	this.group = MountainMobs.group;
    	this.itemName = "boulderblastscepter";
        this.setup();
        this.textureName = "scepterboulderblast";
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
    		EntityProjectileBase projectile = new EntityBoulderBlast(world, entity);
    		projectile.setBaseDamage((int)(projectile.getDamage(null) * power * 2));
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
        if(repairStack.getItem() == ObjectManager.getItem("boulderblastcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
