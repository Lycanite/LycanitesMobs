package lycanite.lycanitesmobs.api.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityPortal extends EntityProjectileBase {
	// Summoning Portal:
	private double targetX;
	private double targetY;
	private double targetZ;
	public int summonAmount = 0;
	public int summonTick = 0;
	public double portalRange = 32.0D;
	
	// Properties:
	public EntityPlayer shootingEntity;
	public Class summonClass;
	public ItemStaffSummoning portalItem;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityPortal(World world) {
        super(world);
        this.setStats();
    }

    public EntityPortal(World world, EntityPlayer shooter, Class summonClass, ItemStaffSummoning portalItem) {
        super(world, shooter);
        this.shootingEntity = shooter;
        this.summonClass = summonClass;
        this.portalItem = portalItem;
        this.setStats();
    }
    
    public void setStats() {
    	this.entityName = "SummoningPortal";
        this.setProjectileScale(4F);
        this.moveToTarget();
    }
    
    
    // ==================================================
 	//                     Updates
 	// ==================================================
    // ========== Main Update ==========
    @Override
    public void onUpdate() {
    	// Move:
    	if(!this.worldObj.isRemote && !this.isDead && (
    			this.shootingEntity == null || this.portalItem == null
    			|| this.shootingEntity.getItemInUse() == null
    			|| this.shootingEntity.getItemInUse().getItem() != this.portalItem)) {
    		this.setDead();
    		return;
    	}
    	
    	this.moveToTarget();
    	
    	// Client:
    	if(this.worldObj.isRemote) {
    		for(int i = 0; i < 32; ++i) {
        		this.worldObj.spawnParticle("portal",
        				this.posX + (4.0F * this.rand.nextFloat()) - 2.0F,
        				this.posY + (4.0F * this.rand.nextFloat()) - 2.0F,
        				this.posZ + (4.0F * this.rand.nextFloat()) - 2.0F,
        				0.0D, 0.0D, 0.0D);
    		}
    		return;
    	}
    	
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.shootingEntity);
		if(playerExt == null)
			return;
    	
    	// Summon:
    	if(++this.summonTick >= this.portalItem.getRapidTime(null)) {
    		float summonMultiplier = (float)(MobInfo.mobClassToInfo.get(this.summonClass).summonCost + this.portalItem.getSummonCostBoost()) * this.portalItem.getSummonCostMod();
			int summonCost = Math.round((float)playerExt.summonFocusCharge * summonMultiplier);
    		if(this.shootingEntity.capabilities.isCreativeMode || playerExt.summonFocus >= summonCost) {
    			if(this.portalItem.getAdditionalCosts(this.shootingEntity)) {
	    			playerExt.summonFocus -= summonCost;
	    			this.summonAmount += this.portalItem.getSummonAmount();
    			}
    		}
    		this.summonTick = 0;
    	}
    }
    
    
    // ==================================================
  	//                 Summon Creatures
  	// ==================================================
    public boolean summonCreatures() {
    	if(this.worldObj.isRemote)
    		return true;
    	for(int i = 0; i < this.summonAmount; i++) {
	    	Entity entity = null;
			try {
				entity = (Entity)this.summonClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.worldObj});
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "A none entity class type was passed to an EntityPortal, only entities can be summoned from portals!");
				e.printStackTrace();
			}
	    	if(entity == null)
	    		return false;
	    	entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
	    	if(entity instanceof EntityCreatureBase) {
	    		EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
	    		entityCreature.setMinion(true);
	    		entityCreature.setTemporary(this.portalItem.getSummonDuration());
		    	if(entityCreature instanceof EntityCreatureTameable) {
		    		((EntityCreatureTameable)entityCreature).setPlayerOwner(this.shootingEntity);
		    		this.portalItem.applyMinionBehaviour((EntityCreatureTameable)entityCreature);
		    	}
		    	this.portalItem.applyMinionEffects(entityCreature);
	    	}
	    	this.worldObj.spawnEntityInWorld(entity);
    	}
    	boolean summonedCreatures = this.summonAmount > 0;
    	this.summonAmount = 0;
    	return summonedCreatures;
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
    	if(this.shootingEntity != null) {
    		// Get Look Target
	        Vec3 lookDirection = this.shootingEntity.getLookVec();
			this.targetX = this.shootingEntity.posX + (lookDirection.xCoord * this.portalRange);
			this.targetY = this.shootingEntity.posY + (lookDirection.yCoord * this.portalRange);
			this.targetZ = this.shootingEntity.posZ + (lookDirection.zCoord * this.portalRange);
	        
			// Apply Raytrace to Look Target:
			MovingObjectPosition target = Utilities.raytrace(this.worldObj, this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ, this.targetX, this.targetY, this.targetZ, 1.0F, null);
	        if(target != null && target.hitVec != null) {
				this.targetX = target.hitVec.xCoord;
				this.targetY = target.hitVec.yCoord;
				this.targetZ = target.hitVec.zCoord;
	        }
	        
	        this.posY += 1.0D;
			
			// Update Position to Target:
	    	this.posX = this.targetX;
	    	this.posY = this.targetY;
	    	this.posZ = this.targetZ;
        }
    }
    
    // ========== Get Coord Behind ==========
    /** Returns the XYZ coordinate in front or behind this entity (using rotation angle) this entity with the given distance, use a negative distance for behind. **/
    public double[] getFacingPosition(Entity entity, double distance) {
    	double angle = Math.toRadians(this.rotationYaw);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
    	double[] coords = new double[3];
        coords[0] = entity.posX + (distance * xAmount);
        coords[1] = entity.posY;
        coords[2] = entity.posZ + (distance * zAmount);
        return coords;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(MovingObjectPosition movingObjectPos) {}
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName) == null)
     		AssetManager.addTexture(this.entityName, LycanitesMobs.domain, "textures/particles/" + this.entityName.toLowerCase() + ".png");
     	return AssetManager.getTexture(this.entityName);
    }
}
