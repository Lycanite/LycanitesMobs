package com.lycanitesmobs.desertmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import com.lycanitesmobs.desertmobs.DesertMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterScythe extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterScythe() {
        super();
    	this.group = DesertMobs.group;
    	this.itemName = "scythescepter";
        this.setup();
        this.textureName = "scepterscythe";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack par1ItemStack) {
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	EntityProjectileRapidFire projectile = new EntityProjectileRapidFire(EntityThrowingScythe.class, world, entity, 15, 5);
        	world.spawnEntityInWorld(projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("throwingscythe")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
