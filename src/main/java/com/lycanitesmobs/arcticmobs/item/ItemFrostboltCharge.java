package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.arcticmobs.entity.EntityFrostbolt;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFrostboltCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostboltCharge() {
        super();
        this.group = ArcticMobs.group;
        this.itemName = "frostboltcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityFrostbolt(world, entityPlayer);
    }
}
