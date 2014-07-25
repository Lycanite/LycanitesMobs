package lycanite.lycanitesmobs.api.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBase extends Item {
	public static int descriptionWidth = 128;
	
	public String itemName = "Item";
	public String domain = LycanitesMobs.domain;
	public String textureName = "item";
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBase() {
        super();
        this.setMaxStackSize(64);
    }
    
    public void setup() {
        this.setCreativeTab(LycanitesMobs.itemsTab);
    	this.setUnlocalizedName(this.itemName);
        this.textureName = this.itemName.toLowerCase();
        int nameLength = this.textureName.length();
        if(nameLength > 6 && this.textureName.substring(nameLength - 6, nameLength).equalsIgnoreCase("charge")) {
        	this.textureName = this.textureName.substring(0, nameLength - 6);
        }
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String description = this.getDescription(itemStack, entityPlayer, textList, par4);
    	if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, descriptionWidth);
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
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}
    
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Start ==========
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	return itemStack;
    }

    // ========== Using ==========
    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer player, int useRemaining) {
    	super.onUsingTick(itemStack, player, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int useRemaining) {
    	super.onPlayerStoppedUsing(itemStack, world, player, useRemaining);
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }
    
    // ========== Entity Interaction ==========
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity) {
    	return false;
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return 0;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return super.getIsRepairable(itemStack, repairStack);
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
    	AssetManager.addIcon(this.itemName, this.domain, this.textureName, iconRegister);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
