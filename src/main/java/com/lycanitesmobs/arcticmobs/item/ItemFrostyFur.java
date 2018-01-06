package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFrostyFur extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostyFur() {
        super();
        this.group = ArcticMobs.instance.group;
        this.itemName = "frostyfur";
        this.setup();
    }
    
    
	// ==================================================
	//                    Item Use
	// ==================================================
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        else {
            pos = pos.offset(facing);
            if(player.canPlayerEdit(pos, facing, itemStack)) {
                try {
                    Block block = world.getBlockState(pos).getBlock();
                    if(block == Blocks.AIR) {
                        world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, AssetManager.getSound("frostcloud"), SoundCategory.PLAYERS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F, false);
                        world.setBlockState(pos, ObjectManager.getBlock("frostcloud").getDefaultState());
                    }
                    if(!player.capabilities.isCreativeMode)
                        itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
                    return EnumActionResult.SUCCESS;
                }
                catch(Exception e) {}
            }
        }
        return EnumActionResult.FAIL;
    }
}
