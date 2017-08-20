package com.lycanitesmobs.freshwatermobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import com.lycanitesmobs.freshwatermobs.entity.EntityWaterJet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWaterJetCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWaterJetCharge() {
        super();
        this.group = FreshwaterMobs.group;
        this.itemName = "waterjetcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityWaterJet(world, entityPlayer, 20, 10);
    }
}
