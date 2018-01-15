package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobDrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class EntityWraith extends EntityCreatureTameable implements IMob {

    public int detonateTimer = -1;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraith(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 0.6F;
        this.setHeight = 0.8F;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setSpeed(2.0D).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 4.0D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Items.BONE), 1).setMinAmount(2).setMaxAmount(2));
        this.drops.add(new MobDrop(new ItemStack(Items.GUNPOWDER), 0.75F).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new MobDrop(new ItemStack(Items.BLAZE_POWDER), 0.5F).setMinAmount(1).setMaxAmount(2));
	}
    
    
    // ==================================================
   	//                     Updates
   	// ==================================================
    // ========== Living ==========
    @Override
    public void onLivingUpdate() {
        
        // Detonate:
        if(!this.getEntityWorld().isRemote) {
            if(this.detonateTimer == 0) {
                this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, 1, true);
                this.setDead();
            }
            else if(this.detonateTimer > 0) {
                this.detonateTimer--;
                if(this.getEntityWorld().getBlockState(this.getPosition()).getMaterial().isSolid()) {
                    this.detonateTimer = 0;
                }
                else {
                    for (EntityLivingBase entity : this.getNearbyEntities(EntityLivingBase.class, null, 1)) {
                        if (entity == this.getOwner())
                            continue;
                        if (entity instanceof EntityCreatureBase) {
                            EntityCreatureBase entityCreature = (EntityCreatureBase) entity;
                            if (entityCreature.getOwner() != null && entityCreature.getOwner() == this.getOwner())
                                continue;
                        }
                        this.detonateTimer = 0;
                        this.attackEntityAsMob(entity, 4);
                    }
                }
            }
        }

        // Particles:
        if(this.getEntityWorld().isRemote && this.detonateTimer <= 5) {
			for (int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
			}
		}
        
        super.onLivingUpdate();
    }


    // ==================================================
    //                     Attacks
    // ==================================================
	// ========== Melee Attack ==========
	@Override
	public boolean meleeAttack(Entity target, double damageScale) {
		if(!super.meleeAttack(target, damageScale))
			return false;

		// Decay:
		if(ObjectManager.getPotionEffect("decay") != null) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("decay"), this.getEffectDuration(20), 1));
		}

		return true;
	}

    public void chargeAttack() {
        this.leap(5, this.rotationPitch);
        this.detonateTimer = 10;
    }

	// ========== Set Attack Target ==========
	@Override
	public boolean canAttackClass(Class targetClass) {
		if(targetClass.isAssignableFrom(IGroupDemon.class))
			return false;
		return super.canAttackClass(targetClass);
	}
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean isFlying() { return true; }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
		if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int explosionRadius = 1;
			if(this.subspecies != null)
				explosionRadius = 3;
			explosionRadius = Math.max(1, Math.round((float)explosionRadius * (float)this.sizeScale));
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, true);
		}
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
		if(potionEffect.getPotion() == MobEffects.WITHER)
			return false;
		if(ObjectManager.getPotionEffect("decay") != null)
			if(potionEffect.getPotion() == ObjectManager.getPotionEffect("decay")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }


	// ==================================================
	//                        NBT
	// ==================================================
	// ========== Read ===========
	@Override
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		if(nbtTagCompound.hasKey("DetonateTimer")) {
			this.detonateTimer = nbtTagCompound.getInteger("DetonateTimer");
		}
	}

	// ========== Write ==========
	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		if(this.detonateTimer > -1) {
			nbtTagCompound.setInteger("DetonateTimer", this.detonateTimer);
		}
	}
}
