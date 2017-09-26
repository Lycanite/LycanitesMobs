package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ItemInfo;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
}
