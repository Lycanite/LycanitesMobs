package lycanite.lycanitesmobs.api.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomFood extends ItemFood {
	
	public String itemName = "customfood";
	public GroupInfo group = LycanitesMobs.group;
	public String texturePath = "customfood";
	
	protected int effectDuration = 40;
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation) {
		super(feed, saturation, false);
		this.itemName = setItemName;
		this.group = group;
		this.texturePath = setTexturePath;
		this.setMaxStackSize(64);
		this.setCreativeTab(LycanitesMobs.itemsTab);
		this.setUnlocalizedName(itemName);
	}
	public ItemCustomFood(String setItemName, GroupInfo group, int feed, float saturation) {
		this(setItemName, group, setItemName.toLowerCase(), feed, saturation);
	}
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String description = this.getDescription(itemStack, entityPlayer, textList, par4);
    	if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
    				textList.add("\u00a7a" + (String)formattedDescription);
    		}
    	}
    	super.addInformation(itemStack, entityPlayer, textList, par4);
    }
    
    public String getDescription(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	return StatCollector.translateToLocal("item." + this.itemName + ".description");
    }
    
    
	// ==================================================
	//                      Effects
	// ==================================================
    public ItemCustomFood setEffectDuration(int duration) {
    	this.effectDuration = duration;
    	return this;
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
        AssetManager.addIcon(itemName, group, texturePath, iconRegister);
    }
}
