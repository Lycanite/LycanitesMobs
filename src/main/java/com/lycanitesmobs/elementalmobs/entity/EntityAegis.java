package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityAegis extends EntityCreatureTameable implements IMob, IGroupRock, IFusable {

	protected Village village;
	public boolean chestProtection = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAegis(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
		this.isAggressiveByDefault = false;
        this.stepHeight = 1.0F;

		this.chestProtection = ConfigBase.getConfig(this.creatureInfo.group, "general").getBool("Features", "Aegis Chest Protection", this.chestProtection, "Set to false to stop Aegis from protecting village chests.");
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
		this.targetTasks.addTask(3, new EntityAIDefendVillage(this));
		this.targetTasks.addTask(4, new EntityAITargetDefend(this, EntityVillager.class));
        //this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
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
			if (!this.hasAttackTarget() && !this.isPetType("familiar") && this.updateTick % 40 == 0){
				BlockPos protectLocation = null;
				int reputation = 0;
				if(this.hasHome()) {
					protectLocation = this.getHomePosition();
				}
				else if(this.village == null || this.updateTick % 400 == 0) {
					this.village = this.getEntityWorld().getVillageCollection().getNearestVillage(new BlockPos(this), 32);
					if(this.village != null) {
						protectLocation = this.village.getCenter();
					}
				}

				// Monitor Nearest Player:
				if(protectLocation != null) {
					EntityPlayer player = this.getEntityWorld().getNearestAttackablePlayer(this, 64, 32);
					ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
					if (player != null) {
						if(this.village != null) {
							reputation = this.village.getPlayerReputation(player.getUniqueID());
						}
						if (reputation <= 0 && Math.sqrt(player.getDistanceSq(protectLocation)) <= 80)
							if (this.chestProtection && player.openContainer != null && (player.openContainer instanceof ContainerChest)) {
								this.setAttackTarget(player);
								this.setFixateTarget(player);
							}
							else if (extendedPlayer != null && extendedPlayer.justBrokenBlock != null) {
								Block brokenBlock = extendedPlayer.justBrokenBlock.getBlock();
								if (brokenBlock instanceof BlockChest || brokenBlock instanceof BlockDoor || brokenBlock instanceof BlockGlowstone) {
									this.setAttackTarget(player);
									this.setFixateTarget(player);
								}
							}
					}
				}
			}

			if(!this.hasAttackTarget()) {
				this.setBlocking();
			}
		}
    }

	@Override
	public boolean canBeTargetedBy(EntityLivingBase entity) {
		if(entity instanceof EntityIronGolem || entity instanceof EntityVillager) {
			return false;
		}
		return super.canBeTargetedBy(entity);
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
	public boolean canAttackWhileBlocking() {
		return false;
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isDamageTypeApplicable(type, source, damage);
    }


	// ==================================================
	//                   Taking Damage
	// ==================================================
	// ========== On Damage ==========
	/** Called when this mob has received damage. Here a random blocking chance is applied. **/
	@Override
	public void onDamage(DamageSource damageSrc, float damage) {
		if(this.getRNG().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F)
			this.setBlocking();
		super.onDamage(damageSrc, damage);
	}

	// ========== Blocking ==========
	public void setBlocking() {
		this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
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
		if(fusable instanceof EntityJengu) {
			return EntityNymph.class;
		}
		if(fusable instanceof EntityGeonach) {
			return EntityVapula.class;
		}
		if(fusable instanceof EntityArgus) {
			return EntitySpectre.class;
		}
		return null;
	}
}
