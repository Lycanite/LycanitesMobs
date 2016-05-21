package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
	public Item getTabIconItem() {
		if(ObjectManager.getItem("demonspawn") != null)
			return ObjectManager.getItem("demonspawn");
		else if(ObjectManager.getItem("desertspawn") != null)
			return ObjectManager.getItem("desertspawn");
		else if(ObjectManager.getItem("plainsspawn") != null)
			return ObjectManager.getItem("plainsspawn");
		else if(ObjectManager.getItem("swampspawn") != null)
			return ObjectManager.getItem("swampspawn");
		else
			return Items.SPAWN_EGG;
	}
}