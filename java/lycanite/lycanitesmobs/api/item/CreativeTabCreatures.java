package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabCreatures extends CreativeTabs {
	
	// ========== Constructor ==========
	public CreativeTabCreatures(int tabID, String modID) {
		super(tabID, modID);
	}
	
	// ========== Tab Icon ==========
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		if(ObjectManager.getItem("DemonEgg") != null)
			return ObjectManager.getItem("DemonEgg");
		else if(ObjectManager.getItem("DesertEgg") != null)
			return ObjectManager.getItem("DesertEgg");
		else if(ObjectManager.getItem("PlainsEgg") != null)
			return ObjectManager.getItem("PlainsEgg");
		else if(ObjectManager.getItem("SwampEgg") != null)
			return ObjectManager.getItem("SwampEgg");
		else
			return Items.spawn_egg;
	}
}