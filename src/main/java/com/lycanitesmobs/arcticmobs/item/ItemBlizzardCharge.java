package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.arcticmobs.entity.EntityBlizzard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlizzardCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBlizzardCharge() {
        super();
        this.group = ArcticMobs.instance.group;
        this.itemName = "blizzardcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityBlizzard(world, entityPlayer);
    }
}
