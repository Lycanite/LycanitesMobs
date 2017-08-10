package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.infernomobs.entity.EntityScorchfireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScorchfireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScorchfireCharge() {
        super();
        this.group = InfernoMobs.group;
        this.itemName = "scorchfirecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityScorchfireball(world, entityPlayer);
    }
}
