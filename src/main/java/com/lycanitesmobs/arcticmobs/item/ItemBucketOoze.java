package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(this.getDescription(), ItemBase.descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
    				textList.add("\u00a7a" + (String)formattedDescription);
    		}
    	}
    	super.addInformation(itemStack, entityPlayer, textList, par4);
    }
    
    public String getDescription() {
    	return I18n.translateToLocal("item." + this.itemName + ".description");
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
