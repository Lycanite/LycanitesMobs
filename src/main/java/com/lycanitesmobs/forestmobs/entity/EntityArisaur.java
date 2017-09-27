package com.lycanitesmobs.forestmobs.entity;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityArisaur extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupPlant, IGroupHeavy {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArisaur(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 2.9F;
        this.setHeight = 4.2F;
        this.fleeHealthPercent = 1.0F;
        this.isHostileByDefault = false;
        this.solidCollision = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(3, new EntityAIMate(this).setMateDistance(5.0D));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("vegetables"));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D).setStrayDistance(3.0D));
        this.tasks.addTask(6, new EntityAIFollowMaster(this).setSpeed(1.0D).setStrayDistance(18.0F));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 20D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("arisaurmeatraw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("arisaurmeatcooked"))).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.APPLE), 0.5F).setMinAmount(0).setMaxAmount(3));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Water Healing:
        if(this.isInWater())
        	this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 2));
        else if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
        	this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 1));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		if(this.getEntityWorld().getBlockState(new BlockPos(par1, par2 - 1, par3)).getBlock() != Blocks.AIR) {
			IBlockState blocState = this.getEntityWorld().getBlockState(new BlockPos(par1, par2 - 1, par3));
			if(blocState.getMaterial() == Material.GRASS)
				return 10F;
			if(blocState.getMaterial() == Material.GROUND)
				return 7F;
		}
        return super.getBlockPathWeight(par1, par2, par3);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
	    return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	if(damageSrc.getTrueSource() != null) {
    		Item heldItem = null;
    		if(damageSrc.getTrueSource() instanceof EntityPlayer) {
    			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getTrueSource();
	    		if(entityPlayer.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem();
	    		}
    		}
    		else if(damageSrc.getTrueSource() instanceof EntityLiving) {
	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getTrueSource();
	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
	    		}
    		}
    		if(ObjectLists.isAxe(heldItem))
				return 4.0F;
    	}
        return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        if(ObjectManager.getPotionEffect("paralysis") != null)
        	if(potionEffect.getPotion() == ObjectManager.getPotionEffect("paralysis")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityArisaur(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("vegetables", testStack);
    }
}
