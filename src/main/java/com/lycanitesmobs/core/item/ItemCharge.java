package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCharge extends ItemBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public ItemCharge() {
        super();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!player.capabilities.isCreativeMode) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }

        if(!world.isRemote) {
            EntityProjectileBase projectile = this.getProjectile(itemStack, world, player);
            if(projectile == null)
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
            world.spawnEntity(projectile);
            this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return null;
    }
}
