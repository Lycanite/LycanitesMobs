package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityWisp extends EntityCreatureTameable implements IGroupPlant {
	EntityWisp playPartner = null;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWisp(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
		this.justAttackedTime = 80;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
		this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
		this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
		this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupShadow.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		if(!this.getEntityWorld().isRemote) {

			// Light Aura:
			if(!this.isPetType("familiar")) {
				if (this.updateTick % 10 == 0) {
					List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
					for (Object entityObj : aoeTargets) {
						EntityLivingBase target = (EntityLivingBase) entityObj;
						if(target == this) {
							continue;
						}
						if(target instanceof EntityWisp) {
							playPartner = (EntityWisp)target;
							continue;
						}
						this.applyBuffs(target, 1, 1);
						this.applyDebuffs(target, 1, 1);
						if(target instanceof EntityZombie || target instanceof EntitySkeleton) {
							//target.setFire(10);
						}
						if(target instanceof EntityCreatureBase) {
							EntityCreatureBase targetCreature = (EntityCreatureBase)target;
							if(targetCreature.daylightBurns()) {
								//targetCreature.setFire(10);
							}
						}
					}
				}
			}

			// Playing:
			if(this.playPartner == null) {
				if (this.updateTick % 40 == 0) {
					List aoeTargets = this.getNearbyEntities(EntityWisp.class, null, 10);
					for (Object entityObj : aoeTargets) {
						EntityWisp target = (EntityWisp)entityObj;
						if(target != this && this.getAttackTarget() != target) {
							this.playPartner = target;
							break;
						}
					}
				}
			}
			else {
				if(this.playPartner.isDead || this.getAttackTarget() == this.playPartner || this.getDistance(this.playPartner) >= 100) {
					this.playPartner = null;
				}
				else {
					if(this.hasAttackTarget() && this.getPlayerOwner() == this.playPartner.getPlayerOwner()) {
						this.playPartner.setAttackTarget(this.getAttackTarget());
					}
					if(!this.justAttacked() && !this.hasAttackTarget()) {
						if(this.updateTick % this.getRangedCooldown() == 0) {
							this.playPartner.justAttackedTime = 240;
							this.attackRanged(this.playPartner, this.getDistance(this.playPartner));
						}
					}
				}
			}
		}
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityLightBall.class, target, range, 0, new Vec3d(0, 0, 0), 0.5f, 0.5f, 1F);
		super.attackRanged(target, range);
    }

    @Override
	public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity == this.playPartner) {
    		return false;
		}
		if(targetEntity instanceof EntityWisp && this.getPlayerOwner() == ((EntityWisp)targetEntity).getPlayerOwner()) {
    		return false;
		}
    	return super.canAttackEntity(targetEntity);
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
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }


	// ==================================================
	//                   Brightness
	// ==================================================
	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}
}
