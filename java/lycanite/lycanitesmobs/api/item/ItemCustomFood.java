package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.ItemInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

public class ItemCustomFood extends ItemFood {

    /** The various classes of foods, used mainly for generic configurable effect durations. **/
    public static enum FOOD_CLASS {
        NONE(0), RAW(1), COOKED(2), MEAL(3), FEAST(4);
        public final int id;
        private FOOD_CLASS(int value) { this.id = value; }
        public int getValue() { return id; }
    }
	
	public String itemName = "customfood";
	public GroupInfo group = LycanitesMobs.group;
	public String texturePath = "customfood";
    public FOOD_CLASS foodClass = FOOD_CLASS.NONE;

    /** The ID of the potion effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected PotionEffect effect;
    /** The ID of the chance effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected float effectChance;

    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(feed, saturation, false);
		this.itemName = setItemName;
		this.group = group;
		this.texturePath = setTexturePath;
        this.foodClass = foodClass;
		this.setMaxStackSize(64);
		this.setCreativeTab(LycanitesMobs.itemsTab);
		this.setUnlocalizedName(itemName);
	}
	public ItemCustomFood(String setItemName, GroupInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		this(setItemName, group, setItemName.toLowerCase(), feed, saturation, foodClass);
	}
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String description = this.getDescription(itemStack, entityPlayer, textList, par4);
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
    
    public String getDescription(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	return I18n.translateToLocal("item." + this.itemName + ".description");
    }


    // ==================================================
    //                     Effects
    // ==================================================
    public int getEffectDuration() {
        if(this.foodClass == FOOD_CLASS.RAW)
            return ItemInfo.durationRaw * 20;
        else if(this.foodClass == FOOD_CLASS.COOKED)
            return ItemInfo.durationCooked * 20;
        else if(this.foodClass == FOOD_CLASS.MEAL)
            return ItemInfo.durationMeal * 20;
        else if(this.foodClass == FOOD_CLASS.FEAST)
            return ItemInfo.durationFeast * 20;
        return 1;
    }

    public ItemCustomFood setPotionEffect(Potion potion, int duration, int amplifier, float chance) {
        PotionEffect potionEffect = new PotionEffect(potion, duration * 20, amplifier, false, false);
        this.effect = potionEffect;
        this.setPotionEffect(potionEffect, chance);
        return this;
    }

    public ItemCustomFood setAlwaysEdible() {
        super.setAlwaysEdible();
        return this;
    }


    // ==================================================
    //                     Visuals
    // ==================================================
    /*/ ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1) {
        return AssetManager.getSprite(itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        AssetManager.addSprite(itemName, group, texturePath, iconRegister);
    }*/
}
