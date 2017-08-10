package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.demonmobs.DemonMobs;
import com.lycanitesmobs.demonmobs.entity.EntityDoomfireball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterDoomfire extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterDoomfire() {
        super();
    	this.group = DemonMobs.group;
    	this.itemName = "doomfirescepter";
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
        	EntityDoomfireball projectile = new EntityDoomfireball(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("doomfirecharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
