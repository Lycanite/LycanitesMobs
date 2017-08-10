package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.demonmobs.DemonMobs;
import com.lycanitesmobs.demonmobs.entity.EntityDevilstar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDevilstarCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDevilstarCharge() {
        super();
        this.group = DemonMobs.group;
        this.itemName = "devilstarcharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityDevilstar(world, entityPlayer);
    }
}
