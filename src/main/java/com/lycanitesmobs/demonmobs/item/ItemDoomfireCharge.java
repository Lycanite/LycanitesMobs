package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.demonmobs.entity.EntityDoomfireball;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDoomfireCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDoomfireCharge() {
        super();
        this.group = DemonMobs.instance.group;
        this.itemName = "doomfirecharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDoomfireball(world, entityPlayer);
    }
}
