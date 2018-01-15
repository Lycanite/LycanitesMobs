package com.lycanitesmobs.elementalmobs.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.elementalmobs.entity.EntityFrostbolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFrostboltCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostboltCharge() {
        super();
        this.group = ElementalMobs.instance.group;
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
