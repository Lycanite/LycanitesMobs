package com.lycanitesmobs.arcticmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntitySerpix extends EntityCreatureTameable implements IGroupPredator, IGroupIce, IGroupHeavy {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySerpix(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(60));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("serpixtreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.5D).setStaminaTime(100).setRange(12.0F).setMinChaseDistance(8.0F));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(9, new EntityAIBeg(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityBlaze.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityMagmaCube.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 2.0F;
        return 1.0F;
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
        // Type:
        List<EntityProjectileRapidFire> projectiles = new ArrayList<>();

        EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectiles.add(projectileEntry);

        EntityProjectileRapidFire projectileEntry2 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry2.offsetX += 1.0D;
        projectiles.add(projectileEntry2);

        EntityProjectileRapidFire projectileEntry3 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry3.offsetX -= 1.0D;
        projectiles.add(projectileEntry3);

        EntityProjectileRapidFire projectileEntry4 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry4.offsetZ += 1.0D;
        projectiles.add(projectileEntry4);

        EntityProjectileRapidFire projectileEntry5 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry5.offsetZ -= 1.0D;
        projectiles.add(projectileEntry5);

        EntityProjectileRapidFire projectileEntry6 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry6.offsetY += 1.0D;
        projectiles.add(projectileEntry6);

        EntityProjectileRapidFire projectileEntry7 = new EntityProjectileRapidFire(EntityBlizzard.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry7.offsetY -= 10D;
        projectiles.add(projectileEntry7);

        BlockPos launchPos = this.getFacingPosition(4D);
        for(EntityProjectileRapidFire projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= this.height / 4;

            // Accuracy:
            float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.posX - launchPos.getX() + accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
            double d2 = target.posZ - launchPos.getZ() + accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);

            // Launch:
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            projectile.setPosition(launchPos.getX(), launchPos.getY(), launchPos.getZ());
            this.getEntityWorld().spawnEntity(projectile);
        }

        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
   	//                      Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
        if(this.isTamed() && this.isSitting())
            return false;
        IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
        if(blockState.getBlock() != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GROUND) return true;
            if(blockState.getMaterial() == Material.GRASS) return true;
            if(blockState.getMaterial() == Material.LEAVES) return true;
            if(blockState.getMaterial() == Material.SAND) return true;
            if(blockState.getMaterial() == Material.CLAY) return true;
            if(blockState.getMaterial() == Material.SNOW) return true;
            if(blockState.getMaterial() == Material.CRAFTED_SNOW) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
        return false;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isChild();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("ooze")) return false;
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        return super.isPotionApplicable(potionEffect);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntitySerpix(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
    	if(!this.isChild())
    		return false;
        return itemstack.getItem() == ObjectManager.getItem("serpixtreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
