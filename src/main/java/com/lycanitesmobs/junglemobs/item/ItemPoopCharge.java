package com.lycanitesmobs.junglemobs.item;

import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.junglemobs.JungleMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.junglemobs.entity.EntityPoop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPoopCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPoopCharge() {
        super();
        this.group = JungleMobs.instance.group;
        this.itemName = "poopcharge";
        this.setup();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    // ========== Use ==========
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(ItemDye.applyBonemeal(itemStack, world, pos, player, hand)) {
            if(!world.isRemote) {
                world.playEvent(2005, pos, 0);
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityPoop(world, entityPlayer);
    }
}
