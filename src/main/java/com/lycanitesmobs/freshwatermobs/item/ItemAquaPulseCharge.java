package com.lycanitesmobs.freshwatermobs.item;

import com.lycanitesmobs.freshwatermobs.entity.EntityAquaPulse;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAquaPulseCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAquaPulseCharge() {
        super();
        this.group = FreshwaterMobs.instance.group;
        this.itemName = "aquapulsecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAquaPulse(world, entityPlayer);
    }
}
