package lycanite.lycanitesmobs.api.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityProjectileBase extends EntityThrowable {
	public String entityName = "projectile";
	public GroupInfo group;
	
	// Properties:
    public boolean movement = true;
	public byte baseDamage = 1;
	public float projectileScale = 1.0f;
	public int projectileLife = 200;
    public double knockbackChance = 1;
    public boolean pierce = false;
    public boolean pierceBlocks = false;
	
	public boolean waterProof = false;
	public boolean lavaProof = false;

    // Animation:
    public int animationFrame = 0;
    public int animationFrameMax = 0;
	
	// ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileBase(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
        this.setup();
    }

    public EntityProjectileBase(World world, EntityLivingBase entityLiving) {
        super(world, entityLiving);
        this.setSize(0.3125F, 0.3125F);
        this.setup();
    }

    public EntityProjectileBase(World world, double par2, double par4, double par6) {
        super(world, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
        this.setup();
    }
    
    // ========== Setup Projectile ==========
    public void setup() {}
	
    
    // ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void onUpdate() {
        if(this.movement)
    	    super.onUpdate();
    	if(this.isInWeb)
    		this.isInWeb = false;
    	
    	// Terrain Destruction
    	if(!this.worldObj.isRemote) {
    		if(!this.waterProof && this.isInWater())
    			this.setDead();
    		else if(!this.lavaProof && this.isInsideOfMaterial(Material.lava))
    			this.setDead();
    		if(this.projectileLife-- <= 0)
    			this.setDead();
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
     protected void onImpact(MovingObjectPosition movingObjectPosition) {
     	 boolean collided = false;
         boolean entityCollision = false;
         boolean blockCollision = false;
     	
     	// Entity Hit:
     	if(movingObjectPosition.entityHit != null) {
     		if(movingObjectPosition.entityHit == this.getThrower())
     			return;
     		boolean doDamage = true;
 			if(movingObjectPosition.entityHit instanceof EntityLivingBase) {
 				doDamage = this.canDamage((EntityLivingBase)movingObjectPosition.entityHit);
 			}
 			if(doDamage) {
 				this.entityCollision(movingObjectPosition.entityHit);
 				if(movingObjectPosition.entityHit instanceof EntityLivingBase) {
 					EntityLivingBase target = (EntityLivingBase)movingObjectPosition.entityHit;
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
                        if(this.knockbackChance < 1) {
                            if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
                                if(target instanceof EntityLivingBase) {
                                    targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
                                    ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
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
                        if(this.knockbackChance < 1) {
                            if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
                                if(target instanceof EntityLivingBase)
                                    ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(targetKnockbackResistance);
                            }
                        }
 					}
 				}
 			}
 			collided = true;
            entityCollision = true;

     		int i = (int)Math.floor(movingObjectPosition.entityHit.posX);
     		int j = (int)Math.floor(movingObjectPosition.entityHit.posY);
            int k = (int)Math.floor(movingObjectPosition.entityHit.posZ);
            if(!this.worldObj.isRemote && this.canDestroyBlock(i, j, k))
            	this.placeBlock(this.worldObj, i, j, k);
     	}
     	
     	// Block Hit:
     	else {
     		int i = movingObjectPosition.blockX;
     		int j = movingObjectPosition.blockY;
            int k = movingObjectPosition.blockZ;
            if(this.worldObj.getBlock(i, j, k) != null)
            	collided = this.worldObj.getBlock(i, j, k).getCollisionBoundingBoxFromPool(this.worldObj, i, j, k) != null;
             
 	        if(collided) {
                blockCollision = true;
 	            switch(movingObjectPosition.sideHit) {
 		            case 0:
 		                --j;
 		                break;
 		            case 1:
 		                ++j;
 		                break;
 		            case 2:
 		                --k;
 		                break;
 		            case 3:
 		                ++k;
 		                break;
 		            case 4:
 		                --i;
 		                break;
 		            case 5:
 		                ++i;
 	            }
 	            
 	            if(!this.worldObj.isRemote && this.canDestroyBlock(i, j, k))
 	            	this.placeBlock(this.worldObj, i, j, k);
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
		    if(!MinecraftServer.getServer().isPVPEnabled()) {
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
    	 //entityLiving.addPotionEffect(new PotionEffect(Potion.name.id, 3 * 20, 0));
    	 return true;
     }
     
     //========== Can Destroy Block ==========
     public boolean canDestroyBlock(int x, int y, int z) {
    	 return this.worldObj.isAirBlock(x, y, z);
     }
     
     //========== Place Block ==========
     public void placeBlock(World world, int x, int y, int z) {
    	 //world.setBlock(x, y, z, ObjectManager.getBlock("BlockName").blockID);
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
         if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
             this.projectileScale *= ((EntityCreatureBase)this.getThrower()).sizeScale;
     }
     
     public float getProjectileScale() {
         return this.projectileScale;
     }
     
     
     // ==================================================
     //                      Damage
     // ==================================================
     public void setBaseDamage(int newDamage) {
     	this.baseDamage = (byte)newDamage;
     }
     
     public float getDamage(Entity entity) {
    	 float damage = (float)this.baseDamage;
    	 if(this.getThrower() != null && this.getThrower() instanceof EntityCreatureBase)
    		 damage *= ((EntityCreatureBase)this.getThrower()).getAttackDamageScale();
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
     public ResourceLocation getTexture() {
     	if(AssetManager.getTexture(this.entityName) == null)
     		AssetManager.addTexture(this.entityName, this.group, "textures/items/" + this.entityName.toLowerCase() + ".png");
     	return AssetManager.getTexture(this.entityName);
     }
     
     
     // ==================================================
     //                      Sounds
     // ==================================================
     public String getLaunchSound() {
     	return AssetManager.getSound(this.entityName);
     }
}
