package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScepter extends Item {
	public String itemName = "Scepter";
	public String textureName = "scepter";
	public String domain = LycanitesMobs.domain;
    private float damageScale = 1.0F;
    private int weaponFlash = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepter(int itemID) {
        super(itemID - 256);
        this.setMaxStackSize(1);
        this.setMaxDamage(this.getDurability());
        this.setCreativeTab(LycanitesMobs.creativeTab);
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
    	player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    // ========== Using ==========
    // Was onUsingItemTick() but acted weird, using the EventListener to replicate.
    //@Override
    public void onPlayerUsing(ItemStack itemStack, EntityPlayer player, int useRemaining) {
    	if(itemStack == null || player == null || player.worldObj == null)
    		return;
    	int useTime = this.getMaxItemUseDuration(itemStack) - useRemaining;
    	if(useTime > this.getRapidTime(itemStack)) {
    		int rapidRemainder = useTime % this.getRapidTime(itemStack);
    		if(rapidRemainder == 0 && player.worldObj != null) {
    			if(this.rapidAttack(itemStack, player.worldObj, player)) {
		    		this.damageItemRapid(itemStack, player);
		    		this.weaponFlash = Math.max(20, this.getRapidTime(itemStack));
		    	}
    		}
    	}
    	if(useTime >= this.getChargeTime(itemStack))
    		this.weaponFlash = Math.max(20, this.getChargeTime(itemStack));
    	super.onUsingItemTick(itemStack, player, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int useRemaining) {
    	int useTime = this.getMaxItemUseDuration(itemStack) - useRemaining;
    	float power = (float)useTime / (float)this.getChargeTime(itemStack);
    	
    	this.weaponFlash = 0;
    	
    	if((double)power < 0.1D)
            return;
    	if(power > 1.0F)
    		power = 1.0F;
    	
    	if(this.chargedAttack(itemStack, world, player, power)) {
    		this.damageItemCharged(itemStack, player, power);
    		this.weaponFlash = Math.min(20, this.getChargeTime(itemStack));
    	}
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    // ========== Durability ==========
    public void damageItemRapid(ItemStack itemStack, EntityPlayer player) {
        itemStack.damageItem(1, player);
    }
    
    public void damageItemCharged(ItemStack itemStack, EntityPlayer player, float power) {
    	itemStack.damageItem((int)(10 * power), player);
    }
    
    public int getDurability() {
    	return 250;
    }

    // ========== Max Use Duration ==========
    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 72000;
    }

    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return getMaxItemUseDuration(itemStack);
    }

    // ========== Rapid Time ==========
    public int getRapidTime(ItemStack itemStack) {
        return getMaxItemUseDuration(itemStack);
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	//EntityThrowable projectile = new EntityThrowable(world, player);
        	//world.spawnEntityInWorld(projectile);
            //world.playSoundAtEntity(player, ((ICustomProjectile) projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return false;
    }
    
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
    	if(!world.isRemote) {
        	//EntityThrowable projectile = new EntityThrowable(world, player);
    		//projectile.setDamage((int)(projectile.getDamage() * power));
        	//world.spawnEntityInWorld(projectile);
            //world.playSoundAtEntity(player, ((ICustomProjectile) projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return false;
    }
	
    
	// ==================================================
	//                      Stats
	// ==================================================
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.damageScale, 0));
        return multimap;
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return 18;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        //if(repairStack.itemID == -1) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int damage) {
    	if(this.weaponFlash-- > 0) {
            return AssetManager.getIconGroup(this.itemName)[1];
    	}
        return AssetManager.getIconGroup(this.itemName)[0];
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
    	AssetManager.addIconGroup(this.itemName, this.domain, new String[] {this.textureName, this.textureName + "_fire"}, iconRegister);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
