package com.lycanitesmobs.mountainmobs.item;

import com.lycanitesmobs.mountainmobs.MountainMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArcaneLaserStormCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcaneLaserStormCharge() {
        super();
        this.group = MountainMobs.instance.group;
        this.itemName = "arcanelaserstormcharge";
        this.setup();
    }
    
    
    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityArcaneLaserStorm(world, entityPlayer);
    }
}
