package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.gui.GUIMinion;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaffSummoning extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSummoning(String itemName, String textureName) {
        super();
        this.itemName = itemName;
        this.textureName = textureName;
        this.setup();

        this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
            }
        });
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public int getDurability() {
    	return 500;
    }
    
    @Override
    public void damageItemRapid(ItemStack itemStack, EntityLivingBase entity) {
        return;
    }
    
    public void damageItemCharged(ItemStack itemStack, EntityLivingBase entity, float power) {
		ExtendedPlayer playerExt = null;
		if(entity instanceof EntityPlayer) {
			playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
		}
    	if(playerExt != null && playerExt.staffPortal != null) {
            this.damage_item(itemStack, playerExt.staffPortal.summonAmount, entity);
    	}
    }
    
    // ========== Charge Time ==========
    @Override
    public int getChargeTime(ItemStack itemStack) {
        return 1;
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 20;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCostBoost() {
    	return 0;
    }
    public float getSummonCostMod() {
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
    // ========== Summon Amount ==========
    public int getSummonAmount() {
    	return 1;
    }
    
    // ========== Additional Costs ==========
    public boolean getAdditionalCosts(EntityPlayer player) {
    	return true;
    }
    
    // ========== Minion Behaviour ==========
    public void applyMinionBehaviour(EntityCreatureTameable minion, EntityPlayer player) {
    	SummonSet summonSet = ExtendedPlayer.getForPlayer(player).getSelectedSummonSet();
        summonSet.applyBehaviour(minion);
    }
    
    // ========== Minion Effects ==========
    public void applyMinionEffects(EntityCreatureBase minion) {}
    
    // ========== On Stop Using ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entity, int useRemaining) {
    	super.onPlayerStoppedUsing(itemStack, world, entity, useRemaining);
		ExtendedPlayer playerExt = null;
		if(entity instanceof EntityPlayer) {
			playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
		}
		if(playerExt != null) {
			playerExt.staffPortal = null;
		}
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null) {
			// Summon Selected Mob:
			SummonSet summonSet = playerExt.getSelectedSummonSet();
			if(summonSet.isUseable()) {
				if(!player.getEntityWorld().isRemote) {
					playerExt.staffPortal = new EntityPortal(world, player, summonSet.getCreatureClass(), this);
					playerExt.staffPortal.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
			    	world.spawnEntity(playerExt.staffPortal);
				}
			}
			// Open Minion GUI If None Selected:
			else {
				playerExt.staffPortal = null;
				if(!player.getEntityWorld().isRemote)
	    			playerExt.sendAllSummonSetsToPlayer();
				if(player.getEntityWorld().isRemote)
					GUIMinion.openToPlayer(player, playerExt.selectedSummonSet);
			}
		}
        return super.onItemRightClick(world, player, hand);
    }
    
    // ========== Rapid ==========
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	return false;
    }

    private void damage_item(ItemStack itemStack, int amountToDamage, EntityLivingBase entity)
    {
        itemStack.damageItem(amountToDamage, entity);
        if (itemStack.getCount() == 0) {
            if (entity.getHeldItem(EnumHand.MAIN_HAND).equals(itemStack)) {
                entity.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            } else if (entity.getHeldItem(EnumHand.OFF_HAND).equals(itemStack)) {
                entity.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }
    
    // ========== Charged ==========
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	ExtendedPlayer playerExt = null;
    	if(entity instanceof EntityPlayer) {
			playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
		}
    	if(playerExt != null && playerExt.staffPortal != null) {
			int successCount = playerExt.staffPortal.summonCreatures();
            this.damage_item(itemStack, successCount, entity);
			return successCount > 0;
		}
		return false;
    }
    
	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
    	if(repairStack.getItem() == Items.GOLD_INGOT) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
