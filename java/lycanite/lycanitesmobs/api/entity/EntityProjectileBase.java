package lycanite.lycanitesmobs.api.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityProjectileBase extends EntityThrowable {
	public String entityName = "projectile";
	public ILycaniteMod mod;
	
	// Properties:
	public byte baseDamage = 1;
	public float projectileScale = 1.0f;
	public int projectileLife = 200;
	
	public boolean waterProof = false;
	public boolean lavaProof = false;
	
	// ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityProjectileBase(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
        this.setup();
    }

    public EntityProjectileBase(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
        this.setup();
    }

    public EntityProjectileBase(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
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
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
    	return 0.03F;
    }
    
    
    // ==================================================
  	//                     Impact
  	// ==================================================
     @Override
     protected void onImpact(MovingObjectPosition movingObjectPosition) {
     	boolean collided = false;
     	
     	// Entity Hit:
     	if(movingObjectPosition.entityHit != null) {
     		boolean doDamage = true;
 			if(movingObjectPosition.entityHit instanceof EntityLivingBase) {
 				doDamage = this.canDamage((EntityLivingBase)movingObjectPosition.entityHit);
 			}
 			if(doDamage) {
 				this.entityCollision(movingObjectPosition.entityHit);
 				if(movingObjectPosition.entityHit instanceof EntityLivingBase)
 					if(this.entityLivingCollision((EntityLivingBase)movingObjectPosition.entityHit))
 						movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.getDamage((EntityLivingBase)movingObjectPosition.entityHit));
 			}
 			collided = true;
     	}
     	
     	// Block Hit:
     	else {
     		int i = movingObjectPosition.blockX;
     		int j = movingObjectPosition.blockY;
            int k = movingObjectPosition.blockZ;
            if(this.worldObj.getBlock(i, j, k) != null)
            	collided = this.worldObj.getBlock(i, j, k).getCollisionBoundingBoxFromPool(this.worldObj, i, j, k) != null;
             
 	        if(collided) {
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
 	        if(!this.worldObj.isRemote) {
 	            this.setDead();
 	        }
     	}
     }
     
     //========== Do Damage Check ==========
     public boolean canDamage(EntityLivingBase targetEntity) {
    	 EntityLivingBase owner = this.getThrower();
		    if(owner != null) {
		    	
		    	// Player Damage Event:
			    if(owner instanceof EntityPlayer) {
			    	if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)owner, targetEntity))) {
			    		return false;
			    	}
			    }
			    
			    // No PVP
			    if(!MinecraftServer.getServer().isPVPEnabled()) {
			    	if(targetEntity instanceof EntityPlayer)
			    		return false;
			    	if(targetEntity instanceof EntityCreatureTameable) {
			    		EntityCreatureTameable tamedTarget = (EntityCreatureTameable)targetEntity;
			    		if(tamedTarget.isTamed()) {
			    			return false;
			    		}
			    	}
			    }
			    
			    // Friendly Fire:
			    if(owner.isOnSameTeam(targetEntity) && LycanitesMobs.config.getFeatureBool("FriendlyFire"))
			    	return false;
		    }
		    return true;
     }
     
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
    		 return Math.round((float)seconds * (float)((EntityCreatureBase)this.getThrower()).getEffectMultiplier());
    	 return seconds;
     }
     
     
     // ==================================================
     //                      Visuals
     // ==================================================
     public ResourceLocation getTexture() {
     	if(AssetManager.getTexture(this.entityName) == null)
     		AssetManager.addTexture(this.entityName, this.mod.getDomain(), "textures/items/" + this.entityName.toLowerCase() + ".png");
     	return AssetManager.getTexture(this.entityName);
     }
     
     
     // ==================================================
     //                      Sounds
     // ==================================================
     public String getLaunchSound() {
     	return AssetManager.getSound(this.entityName);
     }
}
