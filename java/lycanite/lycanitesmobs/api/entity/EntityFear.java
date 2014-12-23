package lycanite.lycanitesmobs.api.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityFear extends EntityCreatureBase {
    public Entity fearedEntity;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityFear(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 0;
        this.hasStepSound = false;
        this.hasAttackSound = false;
        this.spreadFire = false;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.9F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this).setPauseRate(0));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.38D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {}
	
	
	// ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawn() {
    	return false;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Server Side Only:
        if(this.worldObj.isRemote)
        	return;
        
        // Clean Up:
        if(this.fearedEntity == null || !this.fearedEntity.isEntityAlive() || !(this.fearedEntity instanceof EntityLivingBase)) {
        	this.setDead();
        	return;
        }
        
        EntityLivingBase fearedEntityLiving = (EntityLivingBase)this.fearedEntity;
        if(ObjectManager.getPotionEffect("fear") == null || !fearedEntityLiving.isPotionActive(ObjectManager.getPotionEffect("fear"))) {
        	this.setDead();
	    	return;
		}
        
        // Pickup Entity For Fear Movement Override:
        if(this.canPickupEntity(fearedEntityLiving)) {
        	this.pickupEntity(fearedEntityLiving);
        }
        
        // Set Rotation:
        if(this.hasPickupEntity()) {
        	this.getPickupEntity().rotationYaw = this.rotationYaw;
        	this.getPickupEntity().rotationPitch = this.rotationPitch;
        }
        
        // Follow Fear Target If Not Picked Up:
        if(this.getPickupEntity() == null) {
        	this.setPosition(this.fearedEntity.posX, this.fearedEntity.posY, this.fearedEntity.posZ);
			this.motionX = this.fearedEntity.motionX;
			this.motionY = this.fearedEntity.motionY;
			this.motionZ = this.fearedEntity.motionZ;
			this.fallDistance = 0;
        }
    }
    
    
    // ==================================================
  	//                        Fear
  	// ==================================================
    public void setFearedEntity(Entity feared) {
    	this.fearedEntity = feared;
        this.width = feared.width;
        this.height = feared.height;
        this.setSize(feared.width, feared.height);
        this.noClip = feared.noClip;
		this.setLocationAndAngles(feared.posX, feared.posY, feared.posZ, feared.rotationYaw, feared.rotationPitch);
		
        if(feared instanceof EntityLivingBase && !(feared instanceof EntityPlayer)) {
	        EntityLivingBase fearedEntityLiving = (EntityLivingBase)feared;
	        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(fearedEntityLiving.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue());
        }
    }
	
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	return false;
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        return false;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() {
    	if(this.pickupEntity != null) {
    		if(this.pickupEntity instanceof EntityCreatureBase)
    			return ((EntityCreatureBase)this.pickupEntity).canFly();
    		if(this.pickupEntity instanceof EntityFlying)
    			return true;
    		if(this.pickupEntity instanceof EntityPlayer)
    			return ((EntityPlayer)this.pickupEntity).capabilities.isFlying;
    	}
    	return false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected String getLivingSound() { return AssetManager.getSound("effect_fear"); }

    // ========== Hurt ==========
    /** Returns the sound to play when this creature is damaged. **/
    @Override
    protected String getHurtSound() { return AssetManager.getSound("effect_fear"); }

    // ========== Death ==========
    /** Returns the sound to play when this creature dies. **/
    @Override
    protected String getDeathSound() { return AssetManager.getSound("effect_fear"); }
     
    // ========== Fly ==========
    /** Plays a flying sound, usually a wing flap, called randomly when flying. **/
    protected void playFlySound() {
    	return;
    }
}
