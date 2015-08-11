package lycanite.lycanitesmobs.api.entity;

import java.lang.reflect.Constructor;
import java.util.HashSet;

import lycanite.lycanitesmobs.Utilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EntityProjectileLaser extends EntityProjectileBase {
	// Properties:
	public EntityLivingBase shootingEntity;
    /** The entity that this laser should appear from. **/
	public Entity followEntity;
	public int shootingEntityRef = -1;
	public int shootingEntityID = 11;
	
	public float projectileWidth = 0.2f;
	public float projectileHeight = 0.2f;
	
	// Laser:
	public EntityProjectileLaserEnd laserEnd;
	public int laserEndRef = -1;
	public int laserEndID = 12;
	
	public int laserTime = 100;
	public int laserDelay = 20;
	public float laserRange;
	public float laserWidth;
	public float laserLength = 10;
	public int laserTimeID = 13;

	// Laser End:
    /** If true, this entity will use the attack target position of the entity that has fired this if possible. **/
    public boolean useEntityAttackTarget = true;
	private double targetX;
	private double targetY;
	private double targetZ;
	
	// Offsets:
	public double offsetX = 0;
	public double offsetY = 0;
	public double offsetZ = 0;
	public int offsetIDStart = 14;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileLaser(World world) {
        super(world);
        this.setStats();
        this.setTime(0);
    }

    public EntityProjectileLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
        super(world, par2, par4, par6);
        this.laserTime = setTime;
        this.laserDelay = setDelay;
        this.setStats();
    }

    public EntityProjectileLaser(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
        this(world, par2, par4, par6, setTime, setDelay);
        this.followEntity = followEntity;
    }

    public EntityProjectileLaser(World world, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
        this(world, par2EntityLivingBase, setTime, setDelay, null);
    }

    public EntityProjectileLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay, Entity followEntity) {
        super(world, entityLiving);
        this.shootingEntity = entityLiving;
        this.laserTime = setTime;
        this.laserDelay = setDelay;
        this.setStats();
        this.followEntity = followEntity;
        this.syncOffset();
    }
    
    public void setStats() {
        this.setSize(projectileWidth, projectileHeight);
        this.setRange(16.0F);
        this.setLaserWidth(1.0F);
        this.knockbackChance = 0D;
        this.targetX = this.posX;
        this.targetY = this.posY;
        this.targetZ = this.posZ;
        this.dataWatcher.addObject(this.shootingEntityID, this.shootingEntityRef);
        this.dataWatcher.addObject(this.laserEndID, this.laserEndRef);
        this.dataWatcher.addObject(this.laserTimeID, this.laserTime);
        this.dataWatcher.addObject(this.offsetIDStart, (float)this.offsetX);
        this.dataWatcher.addObject(this.offsetIDStart + 1, (float)this.offsetY);
        this.dataWatcher.addObject(this.offsetIDStart + 2, (float)this.offsetZ);
    }
	
    
    // ==================================================
 	//                   Properties
 	// ==================================================
	public void setOffset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		this.syncOffset();
	}
    
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	if(!this.worldObj.isRemote) {
    		this.dataWatcher.updateObject(laserTimeID, this.laserTime);
    	}
    	else {
    		this.laserTime = this.dataWatcher.getWatchableObjectInt(this.laserTimeID);
    	}
    	this.syncShootingEntity();
    	
    	//this.syncOffset(); Broken? :(
    	if(!this.worldObj.isRemote && this.shootingEntity != null) {
    		Entity entityToFollow = this.shootingEntity;
    		if(this.followEntity != null)
    			entityToFollow = this.followEntity;
    		this.posX = entityToFollow.posX + this.offsetX;
    		this.posY = entityToFollow.posY + this.offsetY;
    		this.posZ = entityToFollow.posZ + this.offsetZ;
    	}
    	
    	if(this.laserTime > 0) {
	    	this.updateEnd();
	    	this.laserTime--;
	    	
	    	if(this.laserEnd != null) {
	    		if(this.posX - this.width < this.laserEnd.posX - this.laserEnd.width)
	    			this.boundingBox.minX = this.posX - this.width;
	    		else
	    			this.boundingBox.minX = this.laserEnd.posX - this.laserEnd.width;
	    		
	    		if(this.posX + this.width > this.laserEnd.posX + this.laserEnd.width)
	    			this.boundingBox.maxX = this.posX + this.width;
	    		else
	    			this.boundingBox.maxX = this.laserEnd.posX + this.laserEnd.width;
	    		
	    		
	    		if(this.posY - this.height < this.laserEnd.posY - this.laserEnd.height)
	    			this.boundingBox.minY = this.posY - this.height;
	    		else
	    			this.boundingBox.minY = this.laserEnd.posY - this.laserEnd.height;
	    		
	    		if(this.posY + this.width > this.laserEnd.posY + this.laserEnd.height)
	    			this.boundingBox.maxY = this.posY + this.height;
	    		else
	    			this.boundingBox.maxY = this.laserEnd.posY + this.laserEnd.height;
	    		
	    		
	    		if(this.posZ - this.width < this.laserEnd.posZ - this.laserEnd.width)
	    			this.boundingBox.minZ = this.posZ - this.width;
	    		else
	    			this.boundingBox.minZ = this.laserEnd.posZ - this.laserEnd.width;
	    		
	    		if(this.posZ + this.width > this.laserEnd.posZ + this.laserEnd.width)
	    			this.boundingBox.maxZ = this.posZ + this.width;
	    		else
	    			this.boundingBox.maxZ = this.laserEnd.posZ + this.laserEnd.width;
	    	}
	    	else {
	    		this.boundingBox.minX = this.posX - this.width;
	    		this.boundingBox.maxX = this.posX + this.width;
	    		this.boundingBox.minY = this.posY - this.height;
	    		this.boundingBox.maxY = this.posY + this.height;
	    		this.boundingBox.minZ = this.posZ - this.width;
	    		this.boundingBox.maxZ = this.posZ + this.width;
	    	}
    	}
    	else if(!this.isDead) {
    		this.setDead();
    	}
    }
    
    
    // ==================================================
 	//                   Update End
 	// ==================================================
	public void updateEnd() {
		if(this.worldObj.isRemote) {
			this.laserEndRef = this.dataWatcher.getWatchableObjectInt(this.laserEndID);
			Entity possibleLaserEnd = null;
			if(this.laserEndRef != -1)
				possibleLaserEnd = this.worldObj.getEntityByID(this.laserEndRef);
			if(possibleLaserEnd != null && possibleLaserEnd instanceof EntityProjectileLaserEnd)
				this.laserEnd = (EntityProjectileLaserEnd)possibleLaserEnd;
			else {
				this.laserEnd = null;
				return;
			}
		}

		if(this.laserEnd == null)
			fireProjectile();
		
		if(this.laserEnd == null)
			this.laserEndRef = -1;
		else {
			if(!this.worldObj.isRemote)
				this.laserEndRef = this.laserEnd.getEntityId();
			
			// Entity Aiming:
			if(this.shootingEntity != null && this.useEntityAttackTarget) {
				if(this.shootingEntity instanceof EntityCreatureBase && ((EntityCreatureBase)this.shootingEntity).getAttackTarget() != null) {
					EntityLivingBase attackTarget = ((EntityCreatureBase)this.shootingEntity).getAttackTarget();
					this.targetX = attackTarget.posX;
					this.targetY = attackTarget.posY + (attackTarget.height / 2);
					this.targetZ = attackTarget.posZ;
				}
				else {
					Vec3 lookDirection = this.shootingEntity.getLookVec();
					this.targetX = this.shootingEntity.posX + (lookDirection.xCoord * this.laserRange);
					this.targetY = this.shootingEntity.posY + this.shootingEntity.getEyeHeight() + (lookDirection.yCoord * this.laserRange);
					this.targetZ = this.shootingEntity.posZ + (lookDirection.zCoord * this.laserRange);
				}
			}
			
			// Raytracing:
			HashSet<Entity> excludedEntities = new HashSet<Entity>();
			excludedEntities.add(this);
			if(this.shootingEntity != null)
				excludedEntities.add(this.shootingEntity);
			if(this.followEntity != null)
				excludedEntities.add(this.followEntity);
			MovingObjectPosition target = Utilities.raytrace(this.worldObj, this.posX, this.posY, this.posZ, this.targetX, this.targetY, this.targetZ, this.laserWidth, excludedEntities);
			
			// Update Laser End Position:
			double newTargetX = this.targetX;
			double newTargetY = this.targetY;
			double newTargetZ = this.targetZ;
			if(target != null && target.hitVec != null) {
				newTargetX = target.hitVec.xCoord;
				newTargetY = target.hitVec.yCoord;
				newTargetZ = target.hitVec.zCoord;
			}
			this.laserEnd.onUpdateEnd(newTargetX, newTargetY, newTargetZ);
			
			// Damage:
			if(this.laserTime % this.laserDelay == 0 && this.isEntityAlive())
				if(target != null && target.entityHit != null)
					if(this.laserEnd.getDistanceToEntity(target.entityHit) <= (this.laserWidth * 10)) {
						boolean doDamage = true;
						if(target.entityHit instanceof EntityLivingBase) {
							doDamage = this.canDamage((EntityLivingBase)target.entityHit);
						}
						if(doDamage)
							this.updateDamage(target.entityHit);
					}
		}
		
		this.dataWatcher.updateObject(laserEndID, laserEndRef);
		if(this.getBeamSound() != null)
			this.playSound(this.getBeamSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	}
	
    
    // ==================================================
 	//                    Laser Time
 	// ==================================================
	public void setTime(int time) {
		this.laserTime = time;
	}

	public int getTime() {
		return this.laserTime;
	}
    
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.worldObj;
    	if(world.isRemote)
    		return;
    	
		try {
			if(this.shootingEntity == null) {
		    	Constructor constructor = getLaserEndClass().getDeclaredConstructor(new Class[] { World.class, double.class, double.class, double.class, EntityProjectileLaser.class });
		    	constructor.setAccessible(true);
		    	laserEnd = (EntityProjectileLaserEnd)constructor.newInstance(new Object[] { world, this.posX, this.posY, this.posZ, this });
		    }
	        else {
		    	Constructor constructor = getLaserEndClass().getDeclaredConstructor(new Class[] { World.class, EntityLivingBase.class, EntityProjectileLaser.class });
		    	constructor.setAccessible(true);
		    	laserEnd = (EntityProjectileLaserEnd)constructor.newInstance(new Object[] { world, this.shootingEntity, this });
	        }
	        
			if(this.getLaunchSound() != null)
				this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	        
	        world.spawnEntityInWorld(laserEnd);
		}
		catch (Exception e) {
			System.out.println("[WARNING] [LycanitesMobs] EntityLaser was unable to instantiate the EntityLaserEnd.");
			e.printStackTrace();
		}
    }
	
    
    // ==================================================
 	//               Sync Shooting Entity
 	// ==================================================
    public void syncShootingEntity() {
    	if(!this.worldObj.isRemote) {
    		if(this.shootingEntity == null) this.shootingEntityRef = -1;
    		else this.shootingEntityRef = this.shootingEntity.getEntityId();
    		this.dataWatcher.updateObject(this.shootingEntityID, this.shootingEntityRef);
    	}
    	else {
    		this.shootingEntityRef = this.dataWatcher.getWatchableObjectInt(this.shootingEntityID);
    		if(this.shootingEntityRef == -1) this.shootingEntity = null;
    		else {
    			Entity possibleShootingEntity = this.worldObj.getEntityByID(this.shootingEntityRef);
    			if(possibleShootingEntity != null && possibleShootingEntity instanceof EntityLivingBase)
    				this.shootingEntity = (EntityLivingBase)possibleShootingEntity;
    			else
    				this.shootingEntity = null;
    		}
    	}
    }
    
    public void syncOffset() {
    	if(!this.worldObj.isRemote) {
    		this.dataWatcher.updateObject(this.offsetIDStart, (float)this.offsetX);
    		this.dataWatcher.updateObject(this.offsetIDStart + 1, (float)this.offsetY);
    		this.dataWatcher.updateObject(this.offsetIDStart + 2, (float)this.offsetZ);
    	}
    	else {
    		this.offsetX = this.dataWatcher.getWatchableObjectFloat(this.offsetIDStart);
    		this.offsetY = this.dataWatcher.getWatchableObjectFloat(this.offsetIDStart + 1);
    		this.offsetZ = this.dataWatcher.getWatchableObjectFloat(this.offsetIDStart + 2);
    	}
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    public EntityProjectileLaserEnd getLaserEnd() {
        return this.laserEnd;
    }

    public Class getLaserEndClass() {
        return EntityProjectileLaserEnd.class;
    }
	
    
    // ==================================================
 	//                    Set Target
 	// ==================================================
    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
    	return;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    public boolean updateDamage(Entity target) {
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
        
        if(target instanceof EntityLivingBase)
        	this.onDamage((EntityLivingBase)target, damageInit, attackSuccess);
    	
        // Restore Knockback:
        if(this.knockbackChance < 1) {
            if(this.knockbackChance <= 0 || this.rand.nextDouble() <= this.knockbackChance) {
                if(target instanceof EntityLivingBase)
                    ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(targetKnockbackResistance);
            }
        }

        return attackSuccess;
    }
    
    
    // ==================================================
 	//                      Stats
 	// ==================================================
    public void setRange(float range) {
    	this.laserRange = range;
    }

    public void setLaserWidth(float width) {
    	this.laserWidth = width;
    }

    public float getLaserWidth() {
    	return this.laserWidth;
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    public ResourceLocation getBeamTexture() {
    	return null;
    }
    
    public double[] getLengths() {
    	if(this.laserEnd == null)
    		return new double[] {0.0D, 0.0D, 0.0D};
    	else
    		return new double[] {
    			this.laserEnd.posX - this.posX,
    			this.laserEnd.posY - this.posY,
    			this.laserEnd.posZ - this.posZ
    		};
    }
    
    public float getLength() {
    	if(this.laserEnd == null)
    		return 0;
    	return this.getDistanceToEntity(this.laserEnd);
    }
    
    public float[] getBeamAngles() {
    	float[] angles = new float[] {0, 0, 0, 0};
    	if(this.laserEnd != null) {
    		float dx = (float)(this.laserEnd.posX - this.posX);
    		float dy = (float)(this.laserEnd.posY - this.posY);
    		float dz = (float)(this.laserEnd.posZ - this.posZ);
			angles[0] = (float)Math.toDegrees(Math.atan2(dz, dy)) - 90;
			angles[1] = (float)Math.toDegrees(Math.atan2(dx, dz));
			angles[2] = (float)Math.toDegrees(Math.atan2(dx, dy)) - 90;
			
			// Distance based x/z rotation:
			float dr = (float)Math.sqrt(dx * dx + dz * dz);
			angles[3] = (float)Math.toDegrees(Math.atan2(dr, dy)) - 90;
		}
    	return angles;
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public String getLaunchSound() {
		return null;
	}
	
	public String getBeamSound() {
		return null;
	}
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	if(nbtTagCompound.hasKey("LaserTime"))
    		this.setTime(nbtTagCompound.getInteger("LaserTime"));
    	if(nbtTagCompound.hasKey("OffsetX"))
    		this.offsetX = nbtTagCompound.getDouble("OffsetX");
    	if(nbtTagCompound.hasKey("OffsetY"))
    		this.offsetY = nbtTagCompound.getDouble("OffsetY");
    	if(nbtTagCompound.hasKey("OffsetZ"))
    		this.offsetZ = nbtTagCompound.getDouble("OffsetZ");
        super.readEntityFromNBT(nbtTagCompound);
    }
    
    // ========== Write ==========
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	nbtTagCompound.setInteger("LaserTime", this.laserTime);
    	nbtTagCompound.setDouble("OffsetX", this.offsetX);
    	nbtTagCompound.setDouble("OffsetY", this.offsetY);
    	nbtTagCompound.setDouble("OffsetZ", this.offsetZ);
        super.writeEntityToNBT(nbtTagCompound);
    }
}
