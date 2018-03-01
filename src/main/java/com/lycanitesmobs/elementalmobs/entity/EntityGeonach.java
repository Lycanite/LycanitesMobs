package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityGeonach extends EntityCreatureTameable implements IMob, IGroupRock, IFusable {
	
	public int geonachBlockBreakRadius = 0;
	public float fireDamageAbsorbed = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeonach(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.geonachBlockBreakRadius = ConfigBase.getConfig(this.creatureInfo.group, "general").getInt("Features", "Rare Geonach Block Break Radius", this.geonachBlockBreakRadius, "Controls how large the Celestial Geonach's block breaking radius is when it is charging towards its target. Set to -1 to disable. For their block breaking radius on spawn, see the ROCK spawn type features instead. Note that this is only for the extremely rare Geonach.");
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackPhaseMax = 3;
        this.justAttackedTime = (short)(10);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIFollowFuse(this).setLostDistance(16));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setLongMemory(true));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySilverfish.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
		this.targetTasks.addTask(7, new EntityAITargetFuse(this));
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

        if(!this.getEntityWorld().isRemote) {
			if (this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")){
				// Random Charging:
				if (this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
					if (this.posY - 1 > this.getAttackTarget().posY)
						this.leap(6.0F, -1.0D, this.getAttackTarget());
					else if (this.posY + 1 < this.getAttackTarget().posY)
						this.leap(6.0F, 1.0D, this.getAttackTarget());
					else
						this.leap(6.0F, 0D, this.getAttackTarget());
					if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.geonachBlockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.posX, (int) this.posY, (int) this.posZ, 10, true, this.geonachBlockBreakRadius);
					}
				}
			}

			// Environmental Transformation:
			if(!this.isTamed()) {
				if (this.updateTick % 40 == 0 && this.isInLava()) {
					this.transform(EntityVolcan.class, null, false);
				}
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(EntityVolcan.class, null, false);
				}
			}
		}

        // Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        0.0D, 0.0D, 0.0D,
                        Blocks.TALLGRASS.getStateId(Blocks.STONE.getDefaultState()));
            }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        // Silverfish Extermination:
        if(this.hasAttackTarget() && this.getAttackTarget() instanceof EntitySilverfish)
            return 4.0F;
        return super.getAISpeedModifier();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Silverfish Extermination:
        if(target instanceof EntitySilverfish) {
            target.setDead();
        }

		this.nextAttackPhase();
        return true;
    }

	@Override
	public int getMeleeCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getMeleeCooldown() * 3;
		return super.getMeleeCooldown();
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() == 2)
			return super.getRangedCooldown() * 3;
		return super.getRangedCooldown();
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
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.getTrueSource() != null) {
            // Silverfish Extermination:
            if(damageSrc.getTrueSource() instanceof EntitySilverfish) {
                return 0F;
            }

            // Pickaxe Damage:
    		Item heldItem = null;
    		if(damageSrc.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
	    		}
    		}
    		if(ObjectLists.isPickaxe(heldItem))
                return 4.0F;
    	}
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
    	if(source.isFireDamage()) {
    		this.fireDamageAbsorbed += damage;
    		return false;
		}
		return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBurn() {
    	// Geonach can now burn in order to heat up and transform into Volcans.
    	return true;
    }


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return EntityVolcan.class;
		}
		if(fusable instanceof EntityJengu) {
			return EntitySpriggan.class;
		}
		if(fusable instanceof EntityAegis) {
			return EntityVapula.class;
		}
		if(fusable instanceof EntityArgus) {
			return EntityTremor.class;
		}
		return null;
	}
}
