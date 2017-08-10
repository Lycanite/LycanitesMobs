package com.lycanitesmobs.desertmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.desertmobs.entity.EntityMudshot;
import com.lycanitesmobs.desertmobs.DesertMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMudshotCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMudshotCharge() {
        super();
        this.group = DesertMobs.group;
        this.itemName = "mudshotcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityMudshot(world, entityPlayer);
    }
}
