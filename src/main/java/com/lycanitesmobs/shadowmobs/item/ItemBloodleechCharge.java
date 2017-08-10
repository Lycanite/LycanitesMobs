package com.lycanitesmobs.shadowmobs.item;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import com.lycanitesmobs.shadowmobs.entity.EntityBloodleech;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBloodleechCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBloodleechCharge() {
        super();
        this.group = ShadowMobs.group;
        this.itemName = "bloodleechcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityBloodleech(world, entityPlayer);
    }
}
