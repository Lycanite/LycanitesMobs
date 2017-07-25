package lycanite.lycanitesmobs.core.item;

import com.google.common.collect.Multimap;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScepter extends ItemBase {
    protected float damageScale = 1.0F;
    protected int weaponFlash = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepter() {
        super();
        this.setMaxStackSize(1);
        this.setMaxDamage(this.getDurability());

        this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
            }
        });
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
    // ========== Prevent Swing ==========
    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
        if(entity instanceof EntityPlayer) {
            entity.setActiveHand(EnumHand.MAIN_HAND);
            return true;
        }
        return super.onEntitySwing(entity, itemStack);
    }
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) {
        return true;
    }

    // ========== Start ==========
    @Override
    public void onItemLeftClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(hand == EnumHand.OFF_HAND)
            return;
        //playerIn.setActiveHand(hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        //if(hand == EnumHand.MAIN_HAND)
            //return new ActionResult(EnumActionResult.PASS, itemStackIn);
        player.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    // ========== Using ==========
    // Was onUsingItemTick() but acted weird, using the EventListener to replicate.
    @Override
    public void onUsingTick(ItemStack itemStack, EntityLivingBase entity, int useRemaining) {
    	if(itemStack == null || entity == null || entity.getEntityWorld() == null)
    		return;
    	int useTime = this.getMaxItemUseDuration(itemStack) - useRemaining;
    	if(useTime > this.getRapidTime(itemStack)) {
    		int rapidRemainder = useTime % this.getRapidTime(itemStack);
    		if(rapidRemainder == 0 && entity.getEntityWorld() != null) {
    			if(this.rapidAttack(itemStack, entity.getEntityWorld(), entity)) {
		    		this.damageItemRapid(itemStack, entity);
		    		this.weaponFlash = Math.max(20, this.getRapidTime(itemStack));
		    	}
    		}
    	}
    	if(useTime >= this.getChargeTime(itemStack))
    		this.weaponFlash = Math.max(20, this.getChargeTime(itemStack));

    	super.onUsingTick(itemStack, entity, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    	int useTime = this.getMaxItemUseDuration(stack) - timeLeft;
    	float power = (float)useTime / (float)this.getChargeTime(stack);
    	
    	this.weaponFlash = 0;
    	
    	if((double)power < 0.1D)
            return;
    	if(power > 1.0F)
    		power = 1.0F;
    	
    	if(this.chargedAttack(stack, worldIn, entityLiving, power)) {
    		this.damageItemCharged(stack, entityLiving, power);
    		this.weaponFlash = Math.min(20, this.getChargeTime(stack));
    	}
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.BOW;
    }

    // ========== Durability ==========
    public void damageItemRapid(ItemStack itemStack, EntityLivingBase entity) {
        itemStack.damageItem(1, entity);
    }
    
    public void damageItemCharged(ItemStack itemStack, EntityLivingBase entity, float power) {
    	itemStack.damageItem((int)(10 * power), entity);
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
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
        	//EntityThrowable projectile = new EntityThrowable(world, player);
        	//world.spawnEntity(projectile);
            //world.playSoundAtEntity(player, ((ICustomProjectile) projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return false;
    }
    
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	if(!world.isRemote) {
        	//EntityThrowable projectile = new EntityThrowable(world, player);
    		//projectile.setDamage((int)(projectile.getDamage() * power));
        	//world.spawnEntity(projectile);
            //world.playSoundAtEntity(player, ((ICustomProjectile) projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return false;
    }
	
    
	// ==================================================
	//                      Stats
	// ==================================================
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.damageScale, 0));
        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
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
    //                     Sounds
    // ==================================================
    public void playSound(ItemStack itemStack, World world, EntityLivingBase entity, float power, EntityProjectileBase projectile) {
        this.playSound(world, entity.posX, entity.posY, entity.posZ, projectile.getLaunchSound(), SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        //if(repairStack.itemID == -1) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
