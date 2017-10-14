package com.lycanitesmobs.desertmobs.item;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.desertmobs.DesertMobs;
import com.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemThrowingScythe extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemThrowingScythe() {
        super();
        this.group = DesertMobs.instance.group;
        this.itemName = "throwingscythe";
        this.setup();
    }
    
    

    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityThrowingScythe(world, entityPlayer);
    }
}
