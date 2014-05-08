package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemFood;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomFood extends ItemFood {
	
	public String itemName = "customfood";
	public String domain = LycanitesMobs.domain;
	public String texturePath = "customfood";
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(String setItemName, String setDomain, String setTexturePath, int feed, float saturation) {
		super(feed, saturation, false);
		itemName = setItemName;
		domain = setDomain;
		texturePath = setTexturePath;
        setMaxStackSize(64);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName(itemName);
	}
	public ItemCustomFood(String setItemName, String setDomain, int feed, float saturation) {
		this(setItemName, setDomain, setItemName.toLowerCase(), feed, saturation);
	}
	
	
    // ==================================================
  	//                     Visuals
  	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1) {
        return AssetManager.getIcon(itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        AssetManager.addIcon(itemName, domain, texturePath, iconRegister);
    }
}
