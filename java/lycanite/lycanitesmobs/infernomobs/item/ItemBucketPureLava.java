package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
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

public class ItemBucketPureLava extends ItemBucket {
	public String itemName;
	public GroupInfo group;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBucketPureLava(Fluid fluid) {
        super(ObjectManager.getBlock("purelava"));
		this.setCreativeTab(LycanitesMobs.itemsTab);
        this.group = InfernoMobs.group;
        this.itemName = "bucketpurelava";
        this.setUnlocalizedName(this.itemName);
        ObjectManager.addBucket(this, ObjectManager.getBlock("purelava"), fluid);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
        String description = this.getDescription();
        if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.descriptionWidth);
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
