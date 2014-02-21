package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ICustomProjectile;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityDemonicSpark extends EntityThrowable implements ICustomProjectile {
	public String entityName = "DemonicSpark";
	public ILycaniteMod mod = DemonMobs.instance;
	
	// Properties:
	public Entity shootingEntity;
	byte damage = 5;
	private float projectileScale = 1f;
	private float projectileWidth = 0.2f;
	private float projectileHeight = 0.2f;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDemonicSpark(World par1World) {
        super(par1World);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityDemonicSpark(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityDemonicSpark(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(projectileWidth, projectileHeight);
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if(this.posY > this.worldObj.getHeight() + 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
    	// Entity Hit:
    	if(par1MovingObjectPosition.entityHit != null) {
    		boolean doDamage = true;
			if(par1MovingObjectPosition.entityHit instanceof EntityLivingBase) {
				EntityLivingBase owner = this.getThrower();
			    if(this.getThrower() != null && owner instanceof EntityPlayer) {
			    	if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)owner, par1MovingObjectPosition.entityHit))) {
			    		doDamage = false;
			    	}
			    }
			}
			if(doDamage) {
				par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)damage);
			}
    	}
    	
    	// Impact Particles:
        for(int i = 0; i < 8; ++i) {
            this.worldObj.spawnParticle("reddust", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
        
        // Remove Projectile:
        if(!this.worldObj.isRemote) {
            this.setDead();
        }
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
    	if(AssetManager.getTexture(this.entityName) == null)
    		AssetManager.addTexture(this.entityName, this.mod.getDomain(), "textures/items/" + this.entityName.toLowerCase() + ".png");
    	return AssetManager.getTexture(this.entityName);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("DemonicSpark");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
