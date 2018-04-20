package com.lycanitesmobs.plainsmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityFeradon extends EntityCreatureRideable implements IGroupPredator {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityFeradon(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("feradontreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIAttackMelee(this));
        this.tasks.addTask(7, this.aiSit);
        this.tasks.addTask(8, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(9, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWander(this));
        this.tasks.addTask(11, new EntityAIBeg(this));
        this.tasks.addTask(12, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(13, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(4, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
        this.targetTasks.addTask(7, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(1, 3));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Leaping:
        if(!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.5D, this.getAttackTarget());
        	}
        }

        // Leap Landing Paralysis:
        if(this.leapedAbilityQueued && !this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityQueued = false;
            this.leapedAbilityReady = true;
        }
        if(this.leapedAbilityReady && this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityReady = false;
            double distance = 4.0D;
            List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isEntityAlive()
						|| possibleTarget == EntityFeradon.this
						|| EntityFeradon.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityFeradon.this.isOnSameTeam(possibleTarget)
						|| !EntityFeradon.this.canAttackClass(possibleTarget.getClass())
						|| !EntityFeradon.this.canAttackEntity(possibleTarget))
					return false;

				return true;
			});
            if(!possibleTargets.isEmpty()) {
                for(EntityLivingBase possibleTarget : possibleTargets) {
                    boolean doDamage = true;
                    if(this.getRider() instanceof EntityPlayer) {
                        if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
                            doDamage = false;
                        }
                    }
                    if(doDamage) {
                        possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 0));
                    }
                }
            }
            this.playAttackSound();
        }
    }

    @Override
    public void riderEffects(EntityLivingBase rider) {
        if(rider.isPotionActive(MobEffects.WEAKNESS))
            rider.removePotionEffect(MobEffects.WEAKNESS);
        if(rider.isPotionActive(MobEffects.MINING_FATIGUE))
            rider.removePotionEffect(MobEffects.MINING_FATIGUE);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(!this.onGround)
    		return 2.0F;
    	return 1.0F;
    }

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }

    // ========== Leap ==========
    @Override
    public void leap(double distance, double leapHeight) {
        super.leap(distance, leapHeight);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    // ========== Leap to Target ==========
    @Override
    public void leap(float range, double leapHeight, Entity target) {
        super.leap(range, leapHeight, target);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(!this.onGround)
            return;
        if(this.abilityToggled)
            return;
        if(this.getStamina() < this.getStaminaCost())
            return;

        this.playJumpSound();
        this.leap(4.0D, 0.5D);

        this.applyStaminaCost();
    }

    @Override
    public float getStaminaCost() {
        return 15;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 1.0F;
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }


    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
	
	
	// ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityFeradon(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemStack) {
    	if(itemStack == null)
    		return false;
    	return itemStack.getItem() == ObjectManager.getItem("feradontreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
