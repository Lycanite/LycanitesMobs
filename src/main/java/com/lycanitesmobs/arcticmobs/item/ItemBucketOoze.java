package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBucketOoze extends ItemBucket {
	public String itemName;
	public GroupInfo group;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBucketOoze(Fluid fluid) {
        super(ObjectManager.getBlock("ooze"));
        this.group = ArcticMobs.group;
        this.itemName = "bucketooze";
        this.setRegistryName(this.group.filename, this.itemName);
        this.setUnlocalizedName(this.itemName);
        this.setCreativeTab(LycanitesMobs.itemsTab);
        ObjectManager.addBucket(this, ObjectManager.getBlock("ooze"), fluid);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String description = this.getDescription(stack, worldIn, tooltip, flagIn);
        if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.descriptionWidth);
            for(Object formattedDescription : formattedDescriptionList) {
                if(formattedDescription instanceof String)
                    tooltip.add("\u00a7a" + formattedDescription);
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
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
