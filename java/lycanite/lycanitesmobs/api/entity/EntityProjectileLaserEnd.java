package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileLaserEnd extends EntityProjectileBase {
	// Laser End:
	private double targetX;
	private double targetY;
	private double targetZ;
	private int posIDStart = 13;
	
	// Properties:
	public EntityLivingBase shootingEntity;
	public EntityProjectileLaser laserEntity;
	private float projectileWidth = 0.2f;
	private float projectileHeight = 0.2f;
	private double projectileSpeed;

    // Datawatcher:
    protected static final DataParameter<Float> POS_X = EntityDataManager.<Float>createKey(EntityProjectileLaserEnd.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POS_Y = EntityDataManager.<Float>createKey(EntityProjectileLaserEnd.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POS_Z = EntityDataManager.<Float>createKey(EntityProjectileLaserEnd.class, DataSerializers.FLOAT);
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileLaserEnd(World world) {
        super(world);
        this.setStats();
    }

    public EntityProjectileLaserEnd(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
        super(world, par2, par4, par6);
        this.laserEntity = laser;
        this.setStats();
    }
    
    public EntityProjectileLaserEnd(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
        super(world, shooter);
        this.shootingEntity = shooter;
        this.laserEntity = laser;
        this.setStats();
    }
    
    public void setStats() {
        this.setSpeed(1.0D);
        this.setSize(projectileWidth, projectileHeight);
        if(laserEntity != null) {
	        this.targetX = this.laserEntity.posX;
	        this.targetY = this.laserEntity.posY;
	        this.targetZ = this.laserEntity.posZ;
        }
        this.dataManager.register(POS_X, (float) this.posX);
        this.dataManager.register(POS_Y, (float) this.posY);
        this.dataManager.register(POS_Z, (float)this.posZ);
        this.noClip = true;
    }
    
    
    // ==================================================
 	//                     Updates
 	// ==================================================
    // ========== Main Update ==========
    @Override
    public void onUpdate() {
    	if(this.worldObj.isRemote) {
    		this.posX = this.dataManager.get(POS_X);
    		this.posY = this.dataManager.get(POS_Y);
    		this.posZ = this.dataManager.get(POS_Z);
    		return;
    	}
    	
    	if((this.laserEntity == null || !this.laserEntity.isEntityAlive()) && !this.isDead)
    		this.setDead();
    	
    	if(this.isEntityAlive())
    		this.moveToTarget();
    	
    	this.dataManager.set(POS_X, (float) this.posX);
    	this.dataManager.set(POS_Y, (float) this.posY);
    	this.dataManager.set(POS_Z, (float) this.posZ);
    }
    
    // ========== End Update ==========
	public void onUpdateEnd(double newTargetX, double newTargetY, double newTargetZ) {
		if(this.worldObj.isRemote)
			return;
		
		this.targetX = newTargetX;
		this.targetY = newTargetY;
		this.targetZ = newTargetZ;
		
		if(this.getLaunchSound() != null)
			this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
	}
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    // ========== Move to Target ==========
    public void moveToTarget() {
    	this.posX = moveCoordToTarget(this.posX, this.targetX, this.laserEntity.posX);
    	this.posY = moveCoordToTarget(this.posY, this.targetY, this.laserEntity.posY);
    	this.posZ = moveCoordToTarget(this.posZ, this.targetZ, this.laserEntity.posZ);
    }
    
    // ========== Move Coord ==========
    public double moveCoordToTarget(double coord, double targetCoord, double originCoord) {
    	double distance = targetCoord - coord;
    	double moveSpeed = this.projectileSpeed;
    	if(distance > 0) {
    		if(distance < moveSpeed + 1)
    			moveSpeed = distance;
    		if((targetCoord - originCoord) > (coord - originCoord))
    			return coord + moveSpeed;
    		else
    			return targetCoord;
    	}
    	else if(distance < 0) {
    		if(distance > -moveSpeed - 1)
    			moveSpeed = -distance;
    		if((targetCoord - originCoord) < (coord - originCoord))
    			return coord - moveSpeed;
    		else
    			return targetCoord;
    	}
    	return targetCoord;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {}
    
    
    // ==================================================
 	//                      Speed
 	// ==================================================
    public void setSpeed(double speed) {
    	this.projectileSpeed = speed;
    }
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	return null;
    }
}
