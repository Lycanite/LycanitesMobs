package com.lycanitesmobs.desertmobs.entity;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityCryptZombie extends EntityCreatureAgeable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCryptZombie(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.spreadFire = true;
        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
            pathNavigateGround.setAvoidSun(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(4, new EntityAIAttackMelee(this));
        this.tasks.addTask(6, new EntityAIMoveVillage(this));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityHusk.class).setCheckSight(false));
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Hunger:
        if(target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.HUNGER, this.getEffectDuration(7), 0));
        }
        
        return true;
    }
    
    // ========== On Kill ==========
    @Override
    public void onKillEntity(EntityLivingBase entityLivingBase) {
        super.onKillEntity(entityLivingBase);

        if(this.getEntityWorld().getDifficulty().getDifficultyId() >= 2 && entityLivingBase instanceof EntityVillager) {
            if (this.getEntityWorld().getDifficulty().getDifficultyId() == 2 && this.rand.nextBoolean()) return;

            EntityVillager entityvillager = (EntityVillager)entityLivingBase;
            EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.getEntityWorld());
            entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
            this.getEntityWorld().removeEntity(entityvillager);
            entityzombievillager.onInitialSpawn(this.getEntityWorld().getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityCreatureBase.GroupData(false));
            entityzombievillager.setProfession(entityvillager.getProfession());
            entityzombievillager.setChild(entityvillager.isChild());
            entityzombievillager.setNoAI(entityvillager.isAIDisabled());

            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
                entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
            }

            this.getEntityWorld().spawnEntity(entityzombievillager);
            this.getEntityWorld().playEvent(null, 1016, entityzombievillager.getPosition(), 0);
        }
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityCryptZombie(this.getEntityWorld());
	}
}
