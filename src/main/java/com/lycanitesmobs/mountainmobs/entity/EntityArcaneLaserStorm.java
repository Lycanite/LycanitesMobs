package com.lycanitesmobs.mountainmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityArcaneLaserStorm extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;
	private float projectileWidth = 1f;
	private float projectileHeight = 1f;
	public int expireTime = 15;
    public int laserMax = 7;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityArcaneLaserStorm(World world) {
        super(world);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityArcaneLaserStorm(World world, EntityLivingBase entityLiving) {
        super(world, entityLiving);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityArcaneLaserStorm(World world, double par2, double par4, double par6) {
        super(world, par2, par4, par6);
        this.setSize(projectileWidth, projectileHeight);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaserstorm";
    	this.group = MountainMobs.group;
    	this.setBaseDamage(10);
    	this.setProjectileScale(4F);
        this.pierce = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if(!this.worldObj.isRemote) {
	    	updateLasers();
    	}
    	
    	if(this.posY > this.worldObj.getHeight() + 20)
    		this.setDead();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    List<EntityProjectileLaser> lasers = new ArrayList<EntityProjectileLaser>();
    int laserTick = 0;
    public void updateLasers() {
    	World world = this.worldObj;

        while(this.lasers.size() < this.laserMax) {
            EntityProjectileLaser laser;
            if(this.getThrower() != null) {
                laser = new EntityArcaneLaser(world, this.getThrower(), 20, 10, this);
                laser.posX = this.posX;
                laser.posY = this.posY;
                laser.posZ = this.posZ;
            }
            else
                laser = new EntityArcaneLaser(world, this.posX, this.posY, this.posZ, 20, 10, this);
            laser.useEntityAttackTarget = false;
            this.lasers.add(laser);
            world.spawnEntityInWorld(laser);
        }

        int laserCount = 0;
        for(EntityProjectileLaser laser : this.lasers) {
            laser.setTime(20);
            double[] target = new double[]{this.posX, this.posY, this.posZ};

            if(laserCount == 0)
                target = this.getFacingPosition(this, laser.laserLength, 135);
            if(laserCount == 1)
                target = this.getFacingPosition(this, laser.laserLength, 90);
            if(laserCount == 2)
                target = this.getFacingPosition(this, laser.laserLength, 45);
            if(laserCount == 3)
                target = this.getFacingPosition(this, laser.laserLength, 0);
            if(laserCount == 4)
                target = this.getFacingPosition(this, laser.laserLength, -45);
            if(laserCount == 5)
                target = this.getFacingPosition(this, laser.laserLength, -90);
            if(laserCount == 6)
                target = this.getFacingPosition(this, laser.laserLength, -135);

            if(laserCount == 0 || laserCount == 2 || laserCount == 4 || laserCount == 6)
                target[1] -= laser.laserLength / 2;
            else
                target[1] += laser.laserLength / 2;

            target[0] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;
            target[1] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;
            target[2] += (MathHelper.cos(this.laserTick * 0.25F) * 1.0F) - 0.5F;

            laser.setTarget(target[0], target[1], target[2]);
            laserCount++;
        }

        this.laserTick++;
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Impact Splash/Ricochet ==========
    @Override
    public void onImpact() {
    	super.onImpact();
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound(this.entityName);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
