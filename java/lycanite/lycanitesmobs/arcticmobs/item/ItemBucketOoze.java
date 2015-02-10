package lycanite.lycanitesmobs.arcticmobs.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

public class ItemBucketOoze extends ItemBucket {
	public String itemName;
	public GroupInfo group;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBucketOoze(Fluid fluid) {
        super(ObjectManager.getBlock("ooze"));
		this.setCreativeTab(LycanitesMobs.itemsTab);
        this.group = ArcticMobs.group;
        this.itemName = "bucketooze";
        this.setUnlocalizedName(this.itemName);
        ObjectManager.addBucket(this, ObjectManager.getBlock("ooze"), fluid);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String description = this.getDescription();
    	if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(this.getDescription(), ItemBase.descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
    				textList.add("\u00a7a" + (String)formattedDescription);
    		}
    	}
    	super.addInformation(itemStack, entityPlayer, textList, par4);
    }
    
    public String getDescription() {
    	return StatCollector.translateToLocal("item." + this.itemName + ".description");
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
    	return AssetManager.getIcon(this.itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.itemName, this.group, this.itemName, iconRegister);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
