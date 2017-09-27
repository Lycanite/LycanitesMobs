package com.lycanitesmobs.core.block;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockDoubleSlab extends BlockPillar {
    protected String slabName;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoubleSlab(Material material, GroupInfo group, String name, String slabName) {
		super(material, group, name);
		this.setRegistryName(group.filename, name);
        this.slabName = slabName;
	}


    // ==================================================
    //                      Break
    // ==================================================
    //========== Drops ==========
    @Override
    public Item getItemDropped(IBlockState state, Random random, int zero) {
        Block slabBlock = ObjectManager.getBlock(this.slabName);
        if(slabBlock != null)
            return Item.getItemFromBlock(slabBlock);
        return super.getItemDropped(state, random, zero);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return ObjectManager.getBlock(this.slabName) != null ? 2 : 1;
    }
}
