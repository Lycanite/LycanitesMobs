package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemCustomFood extends ItemFood {
	
	public String itemName = "CustomFood";
	public String domain = LycanitesMobs.domain;
	public String texturePath = "customfood";
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(int itemID, String setItemName, String setDomain, String setTexturePath, int feed, float saturation) {
		super(itemID, feed, saturation, false);
		itemName = setItemName;
		domain = setDomain;
		texturePath = setTexturePath;
        setMaxStackSize(64);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName(itemName);
	}
	public ItemCustomFood(int itemID, String setItemName, String setDomain, int feed, float saturation) {
		this(itemID, setItemName, setDomain, setItemName.toLowerCase(), feed, saturation);
	}
	
	
    // ==================================================
  	//                     Visuals
  	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int par1) {
        return AssetManager.getIcon(itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
        AssetManager.addIcon(itemName, domain, texturePath, iconRegister);
    }
}
