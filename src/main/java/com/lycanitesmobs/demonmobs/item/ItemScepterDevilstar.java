package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.demonmobs.DemonMobs;
import com.lycanitesmobs.demonmobs.entity.EntityDevilstar;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterDevilstar extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterDevilstar() {
        super();
    	this.group = DemonMobs.group;
    	this.itemName = "devilstarscepter";
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
        return 2;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
    		EntityDevilstar projectile = new EntityDevilstar(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("devilstarcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
