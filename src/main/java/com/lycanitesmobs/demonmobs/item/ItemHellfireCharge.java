package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.demonmobs.DemonMobs;
import com.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemHellfireCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemHellfireCharge() {
        super();
        this.group = DemonMobs.instance.group;
        this.itemName = "hellfirecharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityHellfireball(world, entityPlayer);
    }
}
