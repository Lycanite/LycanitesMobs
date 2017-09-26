package com.lycanitesmobs.core.block;

import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWallCustom extends BlockWall {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockWallCustom(BlockBase block) {
		super(block);
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + "_wall"));
        this.setUnlocalizedName(block.blockName + "_wall");
	}

    @SideOnly(Side.CLIENT)
	@Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1));
    }
}
