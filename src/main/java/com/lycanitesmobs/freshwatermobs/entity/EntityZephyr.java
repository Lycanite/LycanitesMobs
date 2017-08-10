package com.lycanitesmobs.freshwatermobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupWater;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class EntityZephyr extends EntityCreatureTameable implements IMob, IGroupWater {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityZephyr(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = false;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(20));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(60).setRange(14.0F).setMinChaseDistance(5.0F));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.GUNPOWDER), 0.5F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.GLOWSTONE_DUST), 0.5F).setMaxAmount(5));
	}
	
	
	// ==================================================
    //                       Attacks
    // ==================================================
    // ========== Can Attack ==========
	@Override
	public boolean canAttackClass(Class targetClass) {
		if(targetClass == this.getClass())
			return false;
		return super.canAttackClass(targetClass);
	}
    
	// ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Paralysis:
    	if(target instanceof EntityLivingBase && this.getRNG().nextFloat() >= 0.75F) {
    		if(ObjectManager.getPotionEffect("Paralysis") != null)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Paralysis"), this.getEffectDuration(2), 0));
         }
        
        return true;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
    short aoeAttackTick = 0;
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Static Aura Attack:
        if(!this.worldObj.isRemote && ++aoeAttackTick == 40) {
            aoeAttackTick = 0;
            boolean applyEffect = this.getRNG().nextFloat() >= 0.5F;
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(target != this && this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    target.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackDamage(1));
                    /*if(applyEffect && ObjectManager.getPotionEffect("Paralysis") != null && ObjectManager.getPotionEffect("Paralysis").id < Potion.potionTypes.length)
                        target.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Paralysis").id, this.getEffectDuration(2), 0));*/
                }
            }
        }
        
        // Particles:
        if(this.worldObj.isRemote) {
            this.worldObj.spawnParticle(EnumParticleTypes.CLOUD, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
            
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    this.worldObj.spawnParticle(EnumParticleTypes.CRIT_MAGIC, target.posX + (this.rand.nextDouble() - 0.5D) * (double) target.width, target.posY + this.rand.nextDouble() * (double) target.height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double) target.width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("Penetration") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Penetration")) return false;
        if(ObjectManager.getPotionEffect("Paralysis") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Paralysis")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    // ========== Damage ==========
    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isDamageTypeApplicable(String type) {
    	if("lightning".equalsIgnoreCase(type))
    		return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
