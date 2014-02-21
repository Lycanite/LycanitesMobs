package lycanite.lycanitesmobs.entity;

import java.lang.reflect.Constructor;

import lycanite.lycanitesmobs.api.ICustomProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityRapidFire extends EntityThrowable implements ICustomProjectile {
	// Properties:
	public EntityLivingBase shootingEntity;
	byte damage = 1;
	private float projectileScale = 1f;
	private float projectileWidth = 0.2f;
	private float projectileHeight = 0.2f;
	
	// Rapid Fire:
	private Class projectileClass = null;
	private int rapidTime = 100;
	private int rapidDelay = 5;
	
	// Offsets:
	public double offsetX = 0;
	public double offsetY = 0;
	public double offsetZ = 0;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityRapidFire(Class entityClass, World par1World, int setTime, int setDelay) {
        super(par1World);
        this.setSize(projectileWidth, projectileHeight);
        projectileClass = entityClass;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
    }

    public EntityRapidFire(Class entityClass, World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
        super(par1World, par2, par4, par6);
        this.setSize(projectileWidth, projectileHeight);
        projectileClass = entityClass;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
    }

    public EntityRapidFire(Class entityClass, World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
        super(par1World, par2EntityLivingBase);
        this.setSize(projectileWidth, projectileHeight);
        this.projectileClass = entityClass;
        this.shootingEntity = par2EntityLivingBase;
        this.offsetX = this.posX - par2EntityLivingBase.posX;
        this.offsetY = this.posY - par2EntityLivingBase.posY;
        this.offsetZ = this.posZ - par2EntityLivingBase.posZ;
        this.rapidTime = setTime;
        this.rapidDelay = setDelay;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	if(this.shootingEntity != null) {
    		this.posX = shootingEntity.posX + this.offsetX;
    		this.posY = shootingEntity.posY + this.offsetY;
    		this.posZ = shootingEntity.posZ + this.offsetZ;
    	}
    	if(rapidTime > 0) {
	    	if(projectileClass == null) {
	    		rapidTime = 0;
	    		return;
	    	}
	    	
	    	if(rapidTime % rapidDelay == 0)
	    		fireProjectile();
	    	
	    	rapidTime--;
    	}
    	else if(!this.isDead) {
    		this.setDead();
    	}
    }
	
    
    // ==================================================
 	//                    Add Time
 	// ==================================================
	public void addTime(int addTime) {
		this.rapidTime += addTime;
	}
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.worldObj;
    	if(world.isRemote)
    		return;
    	
		try {
	        IProjectile projectile = null;
	        
	        if(this.shootingEntity == null) {
		    	Constructor constructor = projectileClass.getDeclaredConstructor(new Class[] { World.class, double.class, double.class, double.class });
		    	constructor.setAccessible(true);
	        	projectile = (IProjectile)constructor.newInstance(new Object[] { world, this.posX, this.posY, this.posZ });
				projectile.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1, 1);
	        }
	        else {
		    	Constructor constructor = projectileClass.getDeclaredConstructor(new Class[] { World.class, EntityLivingBase.class });
		    	constructor.setAccessible(true);
	        	projectile = (IProjectile)constructor.newInstance(new Object[] { world, this.shootingEntity });
	        }
	        
	        if(projectile instanceof ICustomProjectile)
	        	this.playSound(((ICustomProjectile) projectile).getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	        
	        world.spawnEntityInWorld((Entity)projectile);
		}
		catch (Exception e) {
			System.out.println("[WARNING] [LycanitesMobs] EntityRapidFire was unable to instantiate the given projectile class.");
			e.printStackTrace();
		}
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
 	//                    Collision
 	// ==================================================
    public boolean canBeCollidedWith() {
        return false;
    }
    
    
    // ==================================================
 	//                     Attacked
 	// ==================================================
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return false;
    }
    
    
    // ==================================================
 	//                      Scale
 	// ==================================================
    @Override
    public void setProjectileScale(float newScale) {
    	projectileScale = newScale;
    }
    
    @Override
    public float getProjectileScale() {
        return projectileScale;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public void setDamage(int newDamage) {
    	damage = (byte)newDamage;
    }
    
    @Override
    public float getDamage() {
        return (float)damage;
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	return null;
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public String getLaunchSound() {
		return null;
	}
}
