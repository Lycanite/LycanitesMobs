package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCreatures extends CreativeTabs {
	
	// ========== Constructor ==========
	public CreativeTabCreatures(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		if(ObjectManager.getItem("demonspawn") != null)
			return new ItemStack(ObjectManager.getItem("demonspawn"));
		else if(ObjectManager.getItem("desertspawn") != null)
			return new ItemStack(ObjectManager.getItem("desertspawn"));
		else if(ObjectManager.getItem("plainsspawn") != null)
			return new ItemStack(ObjectManager.getItem("plainsspawn"));
		else if(ObjectManager.getItem("swampspawn") != null)
			return new ItemStack(ObjectManager.getItem("swampspawn"));
		else
			return new ItemStack(Items.SPAWN_EGG);
	}
}