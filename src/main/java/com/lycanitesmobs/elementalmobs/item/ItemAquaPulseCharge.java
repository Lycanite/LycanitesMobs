package com.lycanitesmobs.elementalmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.elementalmobs.entity.EntityAquaPulse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAquaPulseCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAquaPulseCharge() {
        super();
        this.group = ElementalMobs.instance.group;
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
