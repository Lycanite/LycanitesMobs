package com.lycanitesmobs.core.block;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.ResourceLocation;

public class BlockFenceCustom extends BlockFence {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFenceCustom(BlockBase block) {
		super(block.getDefaultState().getMaterial(), MapColor.STONE);
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + "_fence"));
        this.setUnlocalizedName(block.blockName + "_fence");
        block.copyAttributesTo(this);
        this.setSoundType(block.getSoundType());
	}
}
