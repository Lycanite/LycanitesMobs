package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityProjectileBase extends EntityThrowable {
	public String entityName = "projectile";
	public GroupInfo group;
	
	// Properties:
    public boolean movement = true;
	public byte baseDamage = 1;
	public float projectileScale = 1F;
	public int projectileLife = 200;
    public double knockbackChance = 1;
    public boolean pierce = false;
    public boolean pierceBlocks = false;
	
	public boolean waterProof = false;
	public boolean lavaProof = false;

    // Texture and Animation:
    public int animationFrame = 0;
    public int animationFrameMax = 0;
    public int textureTiling = 1;
    public boolean clientOnly = false;

    // Data Manager:
    protected static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EntityProjectileBase.class, DataSerializers.FLOAT);
	
	// ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileBase(World world) {
        super(world);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
    }

    public EntityProjectileBase(World world, EntityLivingBase entityLiving) {
        super(world, entityLiving);
        this.setHeadingFromThrower(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, 1.5F, 1.0F);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
    }

    public EntityProjectileBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.dataManager.register(SCALE, this.projectileScale);
        this.setProjectileScale(this.projectileScale);
        this.setup();
    }
    
    // ========== Setup Projectile ==========
    public void setup() {

    }
	
    
    // ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void onUpdate() {
        if(!this.movement) {
            this.inGround = false;
            this.timeUntilPortal = this.getPortalCooldown();
        }
        double initX = this.posX;
        double initY = this.posY;
        double initZ = this.posZ;
        super.onUpdate();
        if(!this.movement) {
            this.posX = initX;
            this.posY = initY;
            this.posZ = initZ;
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            this.setPosition(this.posX, this.posY, this.posZ);
        }

    	this.isInWeb = false;
    	
    	// Terrain Destruction
    	if(!this.worldObj.isRemote) {
    		if(!this.waterProof && this.isInWater())
    			this.setDead();
    		else if(!this.lavaProof && this.isInLava())
    			this.setDead();
    	}
        if(!this.worldObj.isRemote || this.clientOnly) {
            if(this.projectileLife-- <= 0)
                this.setDead();
        }

        // Sync Scale:
        if(this.worldObj.isRemote) {
            this.projectileScale = this.dataManager.get(SCALE);
        }

        // Animation:
        if(this.animationFrameMax > 0) {
            if (this.animationFrame == this.animationFrameMax || this.animationFrame < 0)
                this.animationFrame = 0;
            else
                this.animationFrame++;
        }
    }
	
    
    // ==================================================
 	//                      Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
    	return 0.03F;
    }
    
    
    // ==================================================
  	//                       Impact
  	// ==================================================
     @Override
     protected void onImpact(RayTraceResult rayTraceResult) {
     	 boolean collided = false;
         boolean entityCollision = false;
         boolean blockCollision = false;
     	
     	// Entity Hit:
     	if(rayTraceResult.entityHit != null) {
     		if(rayTraceResult.entityHit == this.getThrower())
     			return;
     		boolean doDamage = true;
 			if(rayTraceResult.entityHit instanceof EntityLivingBase) {
 				doDamage = this.canDamage((EntityLivingBase)rayTraceResult.entityHit);
 			}
 			if(doDamage) {
 				this.entityCollision(rayTraceResult.entityHit);
 				if(rayTraceResult.entityHit instanceof EntityLivingBase) {
 					EntityLivingBase target = (EntityLivingBase)rayTraceResult.entityHit;
 					if(this.entityLivingCollision(target)) {
 						//movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.getDamage((EntityLivingBase)movingObjectPosition.entityHit));

                        boolean attackSuccess = false;
 						float damage = this.getDamage(target);
 						float damageInit = damage;
 						double pierceValue = 5.0D;
 						if(this.getThrower() instanceof EntityCreatureBase)
 							pierceValue = ((EntityCreatureBase)this.getThrower()).getPierceValue();
 				        float pierceDamage = 1 + (float)Math.floor(damage / pierceValue);

                        // Prevent Knockback:
                        double targetKnockbackResistance = 0;
                        boolean stopKnockback = false;
                        if(this.knockbackChance < 1) {
                            if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
                                if(target instanceof EntityLivingBase) {
                                    targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
                                    ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
                                    stopKnockback = true;
                                }
                            }
                        }

                        // Deal Damage:
                        if(this.getThrower() instanceof EntityCreatureBase) {
                            EntityCreatureBase creatureThrower = (EntityCreatureBase)this.getThrower();
                            if(damage <= pierceDamage)
                                attackSuccess = target.attackEntityFrom(creatureThrower.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor()).setDamageIsAbsolute(), damage);
                            else {
                                int hurtResistantTimeBefore = target.hurtResistantTime;
                                target.attackEntityFrom(creatureThrower.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor()).setDamageIsAbsolute(), pierceDamage);
                                target.hurtResistantTime = hurtResistantTimeBefore;
                                damage -= pierceDamage;
                                attackSuccess = target.attackEntityFrom(creatureThrower.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(this, this.getThrower())), damage);
                            }
                        }
                        else {
                            if(damage <= pierceDamage)
                                attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
                            else {
                                int hurtResistantTimeBefore = target.hurtResistantTime;
                                target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), pierceDamage);
                                target.hurtResistantTime = hurtResistantTimeBefore;
                                damage -= pierceDamage;
                                attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
                            }
                        }

 				        this.onDamage(target, damageInit, attackSuccess);

                        // Restore Knockback:
                        if(stopKnockback) {
                            if(target instanceof EntityLivingBase)
                                ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
                        }
 					}
 				}
 			}
 			collided = true;
            entityCollision = true;

     		int i = (int)Math.floor(rayTraceResult.entityHit.posX);
     		int j = (int)Math.floor(rayTraceResult.entityHit.posY);
            int k = (int)Math.floor(rayTraceResult.entityHit.posZ);
            BlockPos pos = new BlockPos(i, j, k);
            if(!this.worldObj.isRemote && this.canDestroyBlock(pos))
            	this.placeBlock(this.worldObj, pos);
     	}
     	
     	// Block Hit:
     	else {
     		int i = rayTraceResult.getBlockPos().getX();
     		int j = rayTraceResult.getBlockPos().getY();
            int k = rayTraceResult.getBlockPos().getZ();
            if(this.worldObj.getBlockState(new BlockPos(i, j, k)) != null)
            	collided = this.worldObj.getBlockState(new BlockPos(i, j, k)).getBoundingBox(this.worldObj, new BlockPos(i, j, k)) != null;
             
 	        if(collided) {
                blockCollision = true;
 	            switch(rayTraceResult.sideHit) {
                    case DOWN:
 		                --j;
 		                break;
                    case UP:
 		                ++j;
 		                break;
                    case SOUTH:
 		                --k;
 		                break;
                    case NORTH:
 		                ++k;
 		                break;
                    case WEST:
 		                --i;
 		                break;
                    case EAST:
 		                ++i;
 	            }

                BlockPos pos = new BlockPos(i, j, k);
 	            if(!this.worldObj.isRemote && this.canDestroyBlock(pos))
 	            	this.placeBlock(this.worldObj, pos);
 	        }
     	}
     	
     	if(collided) {
 	    	// Impact Particles:
 	        if(!this.worldObj.isRemote)
 	        	this.onImpact();
 	        else
 	        	this.onImpactVisuals();
 	        
 	        // Remove Projectile:
            boolean entityPierced = this.pierce && entityCollision;
            boolean blockPierced = this.pierceBlocks && blockCollision;
 	        if(!this.worldObj.isRemote && !entityPierced && !blockPierced) {
 	            this.setDead();
 	        }
     	}
     }
     
     //========== Do Damage Check ==========
     public boolean canDamage(EntityLivingBase targetEntity) {
         if(this.worldObj.isRemote)
             return false;

    	 EntityLivingBase owner = this.getThrower();
	     if(owner != null) {

            if(owner instanceof EntityCreatureBase) {
                EntityCreatureBase ownerCreature = (EntityCreatureBase)owner;
                if(!ownerCreature.canAttackEntity(targetEntity))
                    return false;
            }
	    	
	    	// Player Damage Event:
		    if(owner instanceof EntityPlayer) {
		    	if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)owner, targetEntity))) {
		    		return false;
		    	}
		    }
		    
		    // Player PVP:
		    if(!this.worldObj.getMinecraftServer().isPVPEnabled()) {
		    	if(owner instanceof EntityPlayer) {
			    	if(targetEntity instanceof EntityPlayer)
			    		return false;
			    	if(targetEntity instanceof EntityCreatureTameable) {
			    		EntityCreatureTameable tamedTarget = (EntityCreatureTameable)targetEntity;
			    		if(tamedTarget.isTamed()) {
			    			return false;
			    		}
			    	}
		    	}
		    }
		    
		    // Friendly Fire:
		    if(owner.isOnSameTeam(targetEntity) && MobInfo.friendlyFire)
		    	return false;
	    }
	    
	    return true;
     }
     
     //========== On Damage ==========
     public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {}
     
     //========== Entity Collision ==========
     public void entityCollision(Entity entity) {}
     
     //========== Entity Living Collision ==========
     public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	 return true;
     }
     
     //========== Can Destroy Block ==========
     public boolean canDestroyBlock(BlockPos pos) {
    	 return this.worldObj.isAirBlock(pos);
     }
     
     //========== Place Block ==========
     public void placeBlock(World world, BlockPos pos) {
    	 //world.setBlock(pos, ObjectManager.getBlock("BlockName").blockID);
     }
     
     //========== On Impact Splash/Ricochet Server Side ==========
     public void onImpact() {}
     
     //========== On Impact Particles/Sounds Client Side ==========
     public void onImpactVisuals() {
    	 //for(int i = 0; i < 8; ++i)
    		 //this.worldObj.spawnParticle("particlename", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
     }
     
     
     // ==================================================
     //                    Collision
     // ==================================================
     @Override
     public boolean canBeCollidedWith() {
         return false;
     }
     
     
     // ==================================================
     //                     Attacked
     // ==================================================
     @Override
     public boolean attackEntityFrom(DamageSource damageSource, float damage) {
         return false;
     }
     
     
     // ==================================================
     //                      Scale
     // ==================================================
     public void setProjectileScale(float newScale) {
     	 this.projectileScale = newScale;
         this.setSize(newScale, newScale);
         if(this.worldObj.isRemote && !this.clientOnly)
             return;
         if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
             this.projectileScale *= ((EntityCreatureBase)this.getThrower()).sizeScale;
         this.dataManager.set(SCALE, this.projectileScale);
     }
     
     public float getProjectileScale() {
         return this.projectileScale;
     }

    public float getTextureOffsetY() {
        return 0;
    }
     
     
     // ==================================================
     //                      Damage
     // ==================================================
     public void setBaseDamage(int newDamage) {
     	this.baseDamage = (byte)newDamage;
     }
     
     public float getDamage(Entity entity) {
         float damage = (float)this.baseDamage;
         if(this.getThrower() != null) {
             if(this.getThrower() instanceof EntityCreatureBase)
                 damage *= ((EntityCreatureBase) this.getThrower()).getAttackDamageScale();
             else if(this.getThrower() instanceof EntityPlayer && !(entity instanceof EntityPlayer))
                 damage *= 2;
         }
         return damage;
     }
     
     /** When given a base time (in seconds) this will return the scaled time with difficulty and other modifiers taken into account
      * seconds - The base duration in seconds that this effect should last for.
     **/
     public int getEffectDuration(int seconds) {
    	 if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
    		 return ((EntityCreatureBase)this.getThrower()).getEffectDuration(seconds);
    	 return seconds * 20;
     }

    /** When given a base effect strngth value such as a life drain amount, this will return the scaled value with difficulty and other modifiers taken into account
     * value - The base effect strength.
     **/
    public float getEffectStrength(float value) {
        if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
            return ((EntityCreatureBase)this.getThrower()).getEffectStrength(value);
        return value;
    }


    // ==================================================
    //                      Utility
    // ==================================================
    // ========== Get Facing Coords ==========
    /** Returns the XYZ coordinate in front or behind this entity (using its rotation angle) with the given distance, use a negative distance for behind. **/
    public double[] getFacingPosition(double distance) {
        return this.getFacingPosition(this, distance, 0D);
    }

    /** Returns the XYZ coordinate in front or behind the provided entity with the given distance and angle offset (in degrees), use a negative distance for behind. **/
    public double[] getFacingPosition(Entity entity, double distance, double angleOffset) {
        double angle = Math.toRadians(entity.rotationYaw) + angleOffset;
        double xAmount = -Math.sin(angle);
        double zAmount = Math.cos(angle);
        double[] coords = new double[3];
        coords[0] = entity.posX + (distance * xAmount);
        coords[1] = entity.posY;
        coords[2] = entity.posZ + (distance * zAmount);
        return coords;
    }
     
     
     // ==================================================
     //                      Visuals
     // ==================================================
    public String getTextureName() {
        return this.entityName.toLowerCase();
    }

     public ResourceLocation getTexture() {
     	if(AssetManager.getTexture(this.getTextureName()) == null)
     		AssetManager.addTexture(this.getTextureName(), this.group, "textures/items/" + this.getTextureName() + ".png");
     	return AssetManager.getTexture(this.getTextureName());
     }
     
     
     // ==================================================
     //                      Sounds
     // ==================================================
     public SoundEvent getLaunchSound() {
     	return AssetManager.getSound(this.entityName);
     }
}
