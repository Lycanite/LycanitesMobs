package com.lycanitesmobs.shadowmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import com.lycanitesmobs.shadowmobs.entity.EntityBloodleech;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterBloodleech extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterBloodleech() {
        super();
    	this.group = ShadowMobs.group;
    	this.itemName = "bloodleechscepter";
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
        	EntityBloodleech projectile = new EntityBloodleech(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("BloodleechCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
