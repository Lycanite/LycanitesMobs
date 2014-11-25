package lycanite.lycanitesmobs.api.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.ItemInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

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
    protected int effectID;
    /** The amplifier of the potion effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected int effectAmplifier;
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

    public ItemCustomFood setPotionEffect(int id, int amplifier, float chance) {
        this.effectID = id;
        this.effectAmplifier = amplifier;
        this.effectChance = chance;
        return this;
    }

    @Override
    public ItemFood setPotionEffect(int id, int duration, int amplifier, float chance) {
        return this.setPotionEffect(id, amplifier, chance);
    }

    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        if(world.isRemote || this.effectID <= 0)
            return;
        if(player.getRNG().nextFloat() >= this.effectChance)
            return;
        player.addPotionEffect(new PotionEffect(this.effectID, this.getEffectDuration(), this.effectAmplifier));
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
