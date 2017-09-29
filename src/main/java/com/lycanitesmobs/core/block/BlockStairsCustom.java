package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.BlockStairs;
import net.minecraft.util.ResourceLocation;

public class BlockStairsCustom extends BlockStairs {
	public GroupInfo group;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockStairsCustom(BlockBase block) {
		super(block.getDefaultState());
		this.group = block.group;
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + "_stairs"));
        this.setUnlocalizedName(block.blockName + "_stairs");
	}
}
