package com.lycanitesmobs.saltwatermobs.entity;

import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityRaiko extends EntityCreatureRideable implements IMob, IGroupHunter {

    protected EntityAIWander wanderAI;
    protected EntityAIAttackMelee attackAI;
    protected int waterTime = 0;
    protected boolean wantsToLand;
    protected boolean  isLanded;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRaiko(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 7;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.8F;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("raikotreat"))).setTemptDistanceMin(4.0D));
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
        this.tasks.addTask(5, this.attackAI);
        this.wanderAI = new EntityAIWander(this).setPauseRate(0);
        this.tasks.addTask(8, this.wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.42D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 48D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.FEATHER), 1.0F).setMinAmount(3).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.BONE), 0.75F).setMinAmount(1).setMaxAmount(3));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Land/Fly:
        if(!this.getEntityWorld().isRemote) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(this.hasPickupEntity() || (this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean())) {
                    this.leap(1.0D, 1.0D);
                    this.wanderAI.setPauseRate(0);
                    this.isLanded = false;
                }
            }
            else {
                if(this.wantsToLand) {
                    if(!this.isLanded && this.isSafeToLand()) {
                        this.wanderAI.setPauseRate(120);
                        this.isLanded = true;
                    }
                }
                else {
                    if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
            if(this.hasPickupEntity() || this.hasAttackTarget()) {
                this.wantsToLand = false;
            }
        }
        
        // Entity Pickup Update:
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null) {
	    	this.attackAI.setEnabled(!this.hasPickupEntity());
            if(!this.isInWater()) {
                this.waterTime = 0;

                // Random Dropping:
                if(this.hasPickupEntity()) {
                    ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                    if(extendedEntity != null)
                        extendedEntity.setPickedUpByEntity(this);
                    if(this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                        this.dropPickupEntity();
                    }
                }
    	    	
    	    	// Random Swooping:
    	    	else if(this.hasAttackTarget() && this.getDistanceSqToEntity(this.getAttackTarget()) > 2 && this.getRNG().nextInt(20) == 0) {
    	    		if(this.posY - 1 > this.getAttackTarget().posY)
    	    			this.leap(1.0F, -1.0D, this.getAttackTarget());
    	    		else if(this.posY + 1 < this.getAttackTarget().posY)
    	    			this.leap(1.0F, 1.0D, this.getAttackTarget());
    	    	}
            }

            // Burst Out of Water:
            else {
                this.waterTime++;
                if(this.hasPickupEntity() || this.getAir() <= 40) {
	                if(this.waterTime >= (2 * 20)) {
	                    this.waterTime = 0;
	                    this.leap(0.5F, 2.0D);
	                }
                }
                else if(this.hasAttackTarget()) {
                	if(this.waterTime >= (16 * 20)) {
	                    this.waterTime = 4 * 20;
	                    this.leap(0.5F, 2.0D);
                	}
                }
                else if(this.waterTime >= (8 * 20)) {
                    this.waterTime = 4 * 20;
                    this.leap(0.5F, 2.0D);
            	}
            }
        }
    }

    @Override
    public void riderEffects(EntityLivingBase rider) {
        if(rider.isPotionActive(MobEffects.BLINDNESS))
            rider.removePotionEffect(MobEffects.BLINDNESS);
        if(rider.isPotionActive(ObjectManager.getPotionEffect("weight")))
            rider.removePotionEffect(ObjectManager.getPotionEffect("weight"));
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        if(this.hasPickupEntity() && this.getPickupEntity() instanceof EntityPlayer)
            wanderPosition = new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
        return wanderPosition;
    }

    // ========== Get Flight Offset ==========
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;

        if(target instanceof EntityLivingBase && this.getControllingPassenger() == null) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            // Pickup:
            if (this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
        return !this.isLanded || this.hasPickupEntity();
    }

    @Override
    public boolean isStrongSwimmer() { return false; }
    
    @Override
    public void pickupEntity(EntityLivingBase entity) {
    	super.pickupEntity(entity);
        if(this.getEntityWorld().getBlockState(this.getPosition()) != null && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
    	    this.leap(1.0F, 2.0D);
    }

    @Override
    public void dropPickupEntity() {
    	// Drop Weight Effect:
        if(this.hasPickupEntity()) {
            if(ObjectManager.getPotionEffect("weight") != null)
                this.getPickupEntity().addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("weight"), this.getEffectDuration(5), 1));
        }
    	super.dropPickupEntity();
    }
    
    @Override
    public double[] getPickupOffset(Entity entity) {
    	return new double[]{0, -1.0D, 0};
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("weight") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("weight")) return false;
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }

    @Override
    public float getFallResistance() {
        return 100;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }


    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        if(itemStack == null)
            return false;
        return itemStack.getItem() == ObjectManager.getItem("raikotreat");
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("CookedMeat", testStack) || ObjectLists.inItemList("CookedFish", testStack);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        EntityLivingBase nearestTarget = this.getNearestEntity(EntityLivingBase.class, null, 4, true);
        if(this.canPickupEntity(nearestTarget))
            this.pickupEntity(nearestTarget);

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 20;
    }

    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }
}
