package com.lycanitesmobs.elementalmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.elementalmobs.entity.EntityFrostbolt;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterFrostbolt extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterFrostbolt() {
        super();
    	this.group = ElementalMobs.instance.group;
    	this.itemName = "frostboltscepter";
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
        	EntityFrostbolt projectile = new EntityFrostbolt(world, entity);
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
        if(repairStack.getItem() == ObjectManager.getItem("frostboltcharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
