package lycanite.lycanitesmobs;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs {
	
	// ========== Constructor ==========
	public CreativeTab(int tabID, String modID) {
		super(tabID, modID);
		LanguageRegistry.instance().addStringLocalization("itemGroup." + modID, LycanitesMobs.name);
	}
	
	// ========== Tab Icon ==========
	@Override
	public ItemStack getIconItemStack() {
		if(ObjectManager.getItem("HellfireCharge") != null)
			return new ItemStack(ObjectManager.getItem("HellfireCharge"), 1, 0);
		else if(ObjectManager.getItem("JoustMeat") != null)
			return new ItemStack(ObjectManager.getItem("JoustMeat"), 1, 0);
		else if(ObjectManager.getItem("PoisonGland") != null)
			return new ItemStack(ObjectManager.getItem("PoisonGland"), 1, 0);
		else
			return new ItemStack(Item.bone.itemID, 1, 0);
	}
}