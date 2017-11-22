package com.lycanitesmobs.shadowmobs.entity;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityDarkling extends EntityCreatureTameable implements IMob, IGroupShadow {

    // Data Manager:
    protected static final DataParameter<Integer> LATCH_TARGET = EntityDataManager.<Integer>createKey(EntityCreatureBase.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> LATCH_HEIGHT = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> LATCH_ANGLE = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.FLOAT);

    // Latching
    EntityLivingBase latchEntity = null;
    int latchEntityID = 0;
    double latchHeight = 0.5D;
    double latchAngle = 90D;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDarkling(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 2;
        this.hasAttackSound = true;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.5F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(20));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(3, 1));
        }
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.36D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.ENDER_PEARL), 0.25F));
        this.drops.add(new DropRate(new ItemStack(Items.SPIDER_EYE), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.FERMENTED_SPIDER_EYE), 0.25F));
	}

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the datawatcher. **/
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(LATCH_TARGET, 0);
        this.dataManager.register(LATCH_HEIGHT, (float)this.latchHeight);
        this.dataManager.register(LATCH_ANGLE, (float)this.latchAngle);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Leap:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && !this.hasLatchTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());

        // Latch:
        if(this.hasLatchTarget()) {
            this.noClip = true;

            // Movement:
            Vec3d latchPos = this.getFacingPositionDouble(this.getLatchTarget().posX, this.getLatchTarget().posY + (this.getLatchTarget().height * this.latchHeight), this.getLatchTarget().posZ, this.getLatchTarget().width * 0.5D, this.latchAngle);
            this.setPosition(latchPos.x, latchPos.y, latchPos.z);
            double distanceX = this.getLatchTarget().posX - this.posX;
            double distanceZ = this.getLatchTarget().posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float) MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);

            // Server:
            if(!this.getEntityWorld().isRemote) {
                if(this.getLatchTarget().isEntityAlive() && !this.isInWater()) {
                    if (this.updateTick % 40 == 0) {
                        float damage = this.getAttackDamage(1);
                        if (this.meleeAttack(this.getLatchTarget(), damage))
                            this.heal(damage * 2);
                    }
                }
                else {
                    this.setPosition(this.getLatchTarget().posX, this.getLatchTarget().posY, this.getLatchTarget().posZ);
                    this.setLatchTarget(null);
                    this.noClip = false;
                }
            }

            // Client:
            else {
                for(int i = 0; i < 2; ++i) {
                    this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
            this.noClip = false;
    }


    // ==================================================
    //                     Latching
    // ==================================================
    public EntityLivingBase getLatchTarget() {
        try {
            if (this.getEntityWorld().isRemote) {
                this.latchHeight = this.dataManager.get(LATCH_HEIGHT);
                this.latchAngle = this.dataManager.get(LATCH_ANGLE);
                int latchEntityID = this.getDataManager().get(LATCH_TARGET);
                if (latchEntityID != this.latchEntityID) {
                    this.latchEntity = null;
                    this.latchEntityID = latchEntityID;
                    if (latchEntityID != 0) {
                        Entity possilbeLatchEntity = this.getEntityWorld().getEntityByID(latchEntityID);
                        if (possilbeLatchEntity != null && possilbeLatchEntity instanceof EntityLivingBase)
                            this.latchEntity = (EntityLivingBase) possilbeLatchEntity;
                    }
                }
            }
        }
        catch (Exception e) {}
        return this.latchEntity;
    }

    public void setLatchTarget(EntityLivingBase entity) {
        this.latchEntity = entity;
        if(this.getEntityWorld().isRemote)
            return;
        if(entity == null) {
            this.getDataManager().set(LATCH_TARGET, 0);
            return;
        }
        this.getDataManager().set(LATCH_TARGET, entity.getEntityId());
        this.latchHeight = 0.25D + (0.75D * this.getRNG().nextDouble());
        this.latchAngle = 360 * this.getRNG().nextDouble();
        this.dataManager.set(LATCH_HEIGHT, (float) this.latchHeight);
        this.dataManager.set(LATCH_ANGLE, (float) this.latchAngle);
    }

    public boolean hasLatchTarget() {
        return this.getLatchTarget() != null;
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Can Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        if(this.hasLatchTarget())
            return false;
        return super.canAttackClass(targetClass);
    }

    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
        // Disable Knockback:
        double targetKnockbackResistance = 0;
        if(target instanceof EntityLivingBase) {
            targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

        // Melee Attack:
    	if(!super.meleeAttack(target, damageScale))
    		return false;

        // Restore Knockback:
        if(target instanceof EntityLivingBase)
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);

		// Decay:
		if(ObjectManager.getPotionEffect("decay") != null) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("decay"), this.getEffectDuration(20), 1));
		}
    	
    	// Latch:
        if(!this.hasLatchTarget() && target instanceof EntityLivingBase && !this.isInWater()) {
        	this.setLatchTarget((EntityLivingBase) target);
        }
        
        return true;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Stealth
    // ==================================================
    @Override
    public boolean canStealth() {
        if(this.getEntityWorld().isRemote) return false;
        if(this.isMoving()) return false;
        return this.testLightLevel() <= 0;
    }

    @Override
    public void startStealth() {
        if(this.getEntityWorld().isRemote) {
            EnumParticleTypes particle = EnumParticleTypes.SPELL_WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
                this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
        super.startStealth();
    }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
        if(type.equals("inWall"))
            return false;
        return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        if(ObjectManager.getPotionEffect("Fear") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Fear")) return false;
        if(ObjectManager.getPotionEffect("decay") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("decay")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
