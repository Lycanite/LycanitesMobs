package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
	public Item getTabIconItem() {
		if(ObjectManager.getBlock("summoningpedestal") != null)
			return Item.getItemFromBlock(ObjectManager.getBlock("summoningpedestal"));
		else if(ObjectManager.getBlock("demoncrystal") != null)
			return Item.getItemFromBlock(ObjectManager.getBlock("demoncrystal"));
		else if(ObjectManager.getBlock("shadowcrystal") != null)
			return Item.getItemFromBlock(ObjectManager.getBlock("shadowcrystal"));
		else
			return Item.getItemFromBlock(Blocks.OBSIDIAN);
	}
}