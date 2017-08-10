package com.lycanitesmobs.shadowmobs.entity;

import com.google.common.base.Predicate;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.HashMap;
import java.util.List;

public class EntityShade extends EntityCreatureRideable implements IGroupPredator, IGroupHunter, IGroupShadow {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShade(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 10;
        this.hasAttackSound = true;
        this.hasJumpSound = true;
        this.canGrow = false;
        
        this.setWidth = 0.9F;
        this.setHeight = 2.8F;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackTime = 40;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("shadetreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(6, new EntityAIAttackMelee(this).setSpeed(1.5D).setRate(40));
        this.tasks.addTask(7, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityCow.class).setTameTargetting(true));
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPig.class).setTameTargetting(true));
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySheep.class).setTameTargetting(true));
        }
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityPigZombie.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(7, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 40D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 0.5D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 3D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.ENDER_PEARL), 1F).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Blocks.OBSIDIAN), 0.75F).setMaxAmount(5));
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
        if(this.getStamina() < this.getStaminaCost())
            return;

        this.specialAttack();
        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 100;
    }

    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                     Movement
    // ==================================================
    // Mounted Y Offset:
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.85D;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
        if(!super.meleeAttack(target, damageScale))
        	return false;

        // Leech:
        float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 4);
        this.heal(leeching);

        if(this.getRNG().nextFloat() <= 0.1F)
            this.specialAttack();
    	
        return true;
    }

    // ========== Special Attack ==========
    public void specialAttack() {
        // Horrific Howl:
        double distance = 5.0D;
        List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(distance, distance, distance), new Predicate<EntityLivingBase>() {
            @Override
            public boolean apply(EntityLivingBase possibleTarget) {
                if(!possibleTarget.isEntityAlive()
                        || possibleTarget == EntityShade.this
                        || EntityShade.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntityShade.this.isOnSameTeam(possibleTarget)
                        || !EntityShade.this.canAttackClass(possibleTarget.getClass())
                        || !EntityShade.this.canAttackEntity(possibleTarget))
                    return false;
                return true;
            }
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
                    if (ObjectManager.getPotionEffect("fear") != null)
                        possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("fear"), this.getEffectDuration(5), 1));
                    else
                        possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 0));
                }
            }
        }
        this.playAttackSound();
        this.setJustAttacked();
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return true;
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        if(ObjectManager.getPotionEffect("Fear") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Fear")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityShade(this.getEntityWorld());
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
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("shadetreat");
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
