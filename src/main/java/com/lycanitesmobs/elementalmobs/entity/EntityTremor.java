package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTremor extends EntityCreatureTameable implements IMob, IGroupRock {

	private EntityAIAttackMelee meleeAttackAI;

	public int tremorExplosionStrength = 1;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTremor(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

		this.tremorExplosionStrength = ConfigBase.getConfig(this.creatureInfo.group, "general").getInt("Features", "Tremor Explosion Strength", this.tremorExplosionStrength, "Controls the strength of a Tremor's explosion when attacking, set to -1 to disable completely.");
		this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.meleeAttackAI = new EntityAIAttackMelee(this).setLongMemory(true);
        this.tasks.addTask(2, meleeAttackAI);
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistanceSqToEntity(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    	}
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
			}
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Explosion:
		int explosionStrength = Math.max(1, this.tremorExplosionStrength);
		boolean damageTerrain = this.tremorExplosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing");
		if(this.isPetType("familiar")) {
			explosionStrength = 1;
			damageTerrain = false;
		}
		this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionStrength, damageTerrain);

        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
	// ========== Damage ==========
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
		if(source.isExplosion()) {
			this.heal(damage);
			return false;
		}
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isDamageTypeApplicable(type, source, damage);
    }

	@Override
	public boolean isDamageEntityApplicable(Entity entity) {
		if(entity instanceof EntityWither) {
			return false;
		}
		return super.isDamageEntityApplicable(entity);
	}
    
    @Override
    public boolean canBurn() { return false; }

    @Override
	public boolean canBeTargetedBy(EntityLivingBase entity) {
    	if(entity instanceof EntityWither) {
    		return false;
		}
		return super.canBeTargetedBy(entity);
	}
}
