package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.arcticmobs.entity.EntityTundra;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTundraCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTundraCharge() {
        super();
        this.group = ArcticMobs.group;
        this.itemName = "tundracharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityTundra(world, entityPlayer);
    }
}
