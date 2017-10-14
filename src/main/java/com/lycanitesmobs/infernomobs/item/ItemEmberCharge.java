package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEmberCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemEmberCharge() {
        super();
        this.group = InfernoMobs.instance.group;
        this.itemName = "embercharge";
        this.setup();
    }
    
    
    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityEmber(world, entityPlayer);
    }
}
