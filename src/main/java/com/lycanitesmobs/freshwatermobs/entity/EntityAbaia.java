package com.lycanitesmobs.freshwatermobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class EntityAbaia extends EntityCreatureTameable implements IMob, IGroupPredator {

	EntityAIWander wanderAI;
    protected short aoeAttackTick = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAbaia(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 4;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.9D;
        this.canGrow = true;
        
        this.setWidth = 1.8F;
        this.setHeight = 1.8F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false).setRate(10));
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(6, wanderAI.setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.FISH, 1, 1), 0.5F).setMinAmount(1).setMaxAmount(2).setBurningDrop(new ItemStack(Items.COOKED_FISH, 1, 1)));
        this.drops.add(new DropRate(new ItemStack(Items.GLOWSTONE_DUST), 1F).setMaxAmount(8));
    }


    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Static Aura Attack:
        if(!this.getEntityWorld().isRemote && ++this.aoeAttackTick == 40) {
            this.aoeAttackTick = 0;
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(target != this && this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    target.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackDamage(1));
                }
            }
        }

        // Particles:
        if(this.getEntityWorld().isRemote) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);

            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC, target.posX + (this.rand.nextDouble() - 0.5D) * (double) target.width, target.posY + this.rand.nextDouble() * (double) target.height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double) target.width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
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

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(block == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean isStrongSwimmer() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}
    
    
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
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
}
