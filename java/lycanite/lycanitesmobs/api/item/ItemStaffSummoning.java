package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GUIMinion;
import lycanite.lycanitesmobs.api.pets.SummonSet;
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
	public EntityPortal portalEntity;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSummoning() {
        super();
        this.itemName = "summoningstaff";
        this.setup();
        this.textureName = "summoningstaff";

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
    	return 250;
    }
    
    @Override
    public void damageItemRapid(ItemStack itemStack, EntityLivingBase entity) {
        return;
    }
    
    public void damageItemCharged(ItemStack itemStack, EntityLivingBase entity, float power) {
    	if(this.portalEntity != null) {
    		itemStack.damageItem((5 * this.portalEntity.summonAmount), entity);
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
		this.portalEntity = null;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null) {
			// Summon Selected Mob:
			SummonSet summonSet = playerExt.getSelectedSummonSet();
			if(summonSet.isUseable()) {
				if(!player.worldObj.isRemote) {
			    	this.portalEntity = new EntityPortal(world, player, summonSet.getCreatureClass(), this);
			    	this.portalEntity.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
			    	world.spawnEntityInWorld(this.portalEntity);
				}
			}
			// Open Minion GUI If None Selected:
			else {
				this.portalEntity = null;
				if(!player.worldObj.isRemote)
	    			playerExt.sendAllSummonSetsToPlayer();
				if(player.worldObj.isRemote)
					GUIMinion.openToPlayer(player, playerExt.selectedSummonSet);
			}
		}
        return super.onItemRightClick(itemStack, world, player, hand);
    }
    
    // ========== Rapid ==========
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	return false;
    }
    
    // ========== Charged ==========
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
    	if(this.portalEntity != null) {
			boolean success = this.portalEntity.summonCreatures();
			this.portalEntity = null;
			return success;
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
