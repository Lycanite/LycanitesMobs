package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabBlocks extends CreativeTabs {

	// ========== Constructor ==========
	public CreativeTabBlocks(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		if(ObjectManager.getBlock("summoningpedestal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("summoningpedestal")));
		else if(ObjectManager.getBlock("demoncrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("demoncrystal")));
		else if(ObjectManager.getBlock("shadowcrystal") != null)
			return new ItemStack(Item.getItemFromBlock(ObjectManager.getBlock("shadowcrystal")));
		else
			return new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
	}
}