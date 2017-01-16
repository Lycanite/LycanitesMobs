package lycanite.lycanitesmobs.core.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemSwordBase extends ItemSword {
	public static int descriptionWidth = 128;
	
	public String itemName = "Item";
	public GroupInfo group = LycanitesMobs.group;
	public String textureName = "item";
    public final Item.ToolMaterial toolMaterial;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordBase(Item.ToolMaterial toolMaterial) {
        super(toolMaterial);
        this.toolMaterial = toolMaterial;
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
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, descriptionWidth);
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
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

    /** Called from the main EventListener this works the same as onUpdate but is called before the rest of the entity's logic. **/
    public void onEarlyUpdate(ItemStack itemStack, EntityLivingBase entityLiving, EnumHand hand) { }
    
    
	// ==================================================
	//                     Tool/Weapon
	// ==================================================
    // ========== Get Sword Damage ==========
    /**
     * Returns the additional damage provided by this weapon.
     * Seems to be added on to a base value of 4.
     * Most weapons return 0 which would be +4 damage.
     * Diamond returns 3 which is +7 damage.
     * @return
     */
    @Override
    public float getDamageVsEntity() {
        return this.toolMaterial.getDamageVsEntity();
    }

	// ========== Hit Entity ==========
    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityHit, EntityLivingBase entityUser) {
    	return super.hitEntity(itemStack, entityHit, entityUser);
    }
    
	// ========== Block Destruction ==========
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving) {
        return super.onBlockDestroyed(stack, worldIn, blockIn, pos, entityLiving);
    }
    
    // ========== Block Effectiveness ==========
    @Override
    public float getStrVsBlock(ItemStack itemStack, IBlockState state) {
        return super.getStrVsBlock(itemStack, state);
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Use ==========
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
    	return super.onItemRightClick(itemStack, world, player, hand);
    }

    // ========== Using ==========
    @Override
    public void onUsingTick(ItemStack itemStack, EntityLivingBase entity, int useRemaining) {
    	super.onUsingTick(itemStack, entity, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entity, int useRemaining) {
    	super.onPlayerStoppedUsing(itemStack, world, entity, useRemaining);
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return super.getItemUseAction(itemStack);
    }
    
    // ========== Entity Interaction ==========
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
    	return false;
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability(); // Based on material.
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return super.getIsRepairable(itemStack, repairStack);
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    public void onSpawnEntity(Entity entity) {
        return;
    }

    /** Should return a chance from 0.0 to 1.0 which is used for special weapon effects such as randomly spawning minions when hitting enemies. **/
    public float getSpecialEffectChance() { return 0.2F; }

	
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
