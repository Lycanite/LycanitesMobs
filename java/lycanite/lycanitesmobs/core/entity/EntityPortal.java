package lycanite.lycanitesmobs.core.entity;

import lycanite.lycanitesmobs.*;
import lycanite.lycanitesmobs.core.info.MobInfo;
import lycanite.lycanitesmobs.core.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPortal extends EntityProjectileBase {
	// Summoning Portal:
	private double targetX;
	private double targetY;
	private double targetZ;
	public int summonAmount = 0;
	public int summonTick = 0;
    public int summonTime = 5 * 20;
	public double portalRange = 32.0D;
    public int summonDuration = 60 * 20;
	
	// Properties:
	public EntityPlayer shootingEntity;
	public Class summonClass;
	public ItemStaffSummoning portalItem;
    public String ownerName;
    public int ownerNameID;
    public TileEntitySummoningPedestal summoningPedestal;

    // Datawatcher:
    protected static final DataParameter<String> OWNER_NAME = EntityDataManager.<String>createKey(EntityPortal.class, DataSerializers.STRING);
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityPortal(World world) {
        super(world);
        this.setStats();
        this.isImmuneToFire = true;
    }

    public EntityPortal(World world, EntityPlayer shooter, Class summonClass, ItemStaffSummoning portalItem) {
        super(world, shooter);
        this.shootingEntity = shooter;
        this.summonClass = summonClass;
        this.portalItem = portalItem;
        this.setStats();
    }

    public EntityPortal(World world, TileEntitySummoningPedestal summoningPedestal) {
        super(world);
        this.summoningPedestal = summoningPedestal;
        this.setStats();
        this.posX = summoningPedestal.getPos().getX() + 0.5D;
        this.posY = summoningPedestal.getPos().getY() + 1.5D;
        this.posZ = summoningPedestal.getPos().getZ() + 0.5D;
    }
    
    public void setStats() {
    	this.entityName = "summoningportal";
        this.setProjectileScale(6);
        this.moveToTarget();

        this.animationFrameMax = 7;
        this.movement = false;

        this.dataManager.register(OWNER_NAME, "");
    }
    
    
    // ==================================================
 	//                     Updates
 	// ==================================================
    // ========== Main Update ==========
    @Override
    public void onUpdate() {
        if(this.shootingEntity != null || this.summoningPedestal != null)
            this.projectileLife = 5;
        super.onUpdate();

        // ==========Summoning Pedestal ==========
        if(!this.getEntityWorld().isRemote) {
            if(this.summoningPedestal != null) {
                this.shootingEntity = this.summoningPedestal.getPlayer();
                this.summonClass = this.summoningPedestal.getSummonClass();
            }
        }

        // ==========Sync Shooter Name ==========
        if(!this.getEntityWorld().isRemote) {
            // Summoning Staff or Summoning Pedestal (with active player):
            if(this.shootingEntity != null)
                this.dataManager.set(OWNER_NAME, this.shootingEntity.getName());
            // Summoning Pedestal:
            else if(this.summoningPedestal != null)
                this.dataManager.set(OWNER_NAME, this.summoningPedestal.getOwnerName());
            // Wild:
            else
                this.dataManager.set(OWNER_NAME, "");
        }
        else {
            this.ownerName = this.dataManager.get(OWNER_NAME);
        }

    	// ========== Check for Despawn ==========
    	if(!this.getEntityWorld().isRemote && !this.isDead) {
            // Summoning Pedestal:
            if(this.summoningPedestal != null) {
                if(this.summonClass == null) {
                    this.setDead();
                    return;
                }
            }

            // Summoning Staff:
            else {
                if(this.shootingEntity == null || !this.shootingEntity.isEntityAlive() || this.portalItem == null || this.portalItem.portalEntity != this) {
                    this.setDead();
                    return;
                }
            }
    	}
    	
    	// ========== Move ==========
    	this.moveToTarget();

        // ========== Stat Sync ==========
        // Summoning Staff:
        if(this.shootingEntity != null && this.summoningPedestal == null) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.shootingEntity);
            if(playerExt != null && this.portalItem != null) {
                if(++this.summonTick >= this.portalItem.getRapidTime(null)) {
                    this.summonDuration = this.portalItem.getSummonDuration();
                    if(this.shootingEntity.capabilities.isCreativeMode)
                        this.summonAmount += this.portalItem.getSummonAmount();
                    else {
                        float summonMultiplier = (float) (MobInfo.mobClassToInfo.get(this.summonClass).summonCost + this.portalItem.getSummonCostBoost()) * this.portalItem.getSummonCostMod();
                        int summonCost = Math.round((float) playerExt.summonFocusCharge * summonMultiplier);
                        if(playerExt.summonFocus >= summonCost) {
                            if(this.portalItem.getAdditionalCosts(this.shootingEntity)) {
                                playerExt.summonFocus -= summonCost;
                                this.summonAmount += this.portalItem.getSummonAmount();
                            }
                        }
                    }
                    this.summonTick = 0;
                }
            }
        }

        // Summoning Pedestal:
        else if(this.summonClass != null) {
            if (++this.summonTick >= this.summonTime) {
                this.summonAmount = this.summoningPedestal.summonAmount;
                this.summonTick = 0;
            }
        }

        // ========== Client ==========
        if(this.getEntityWorld().isRemote) {
            for(int i = 0; i < 32; ++i) {
                double angle = Math.toRadians(this.rand.nextFloat() * 360);
                float distance = this.rand.nextFloat() * 2;
                double x = distance * Math.cos(angle) + Math.sin(angle);
                double z = distance * Math.sin(angle) - Math.cos(angle);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL,
                        this.posX + x,
                        this.posY + (4.0F * this.rand.nextFloat()) - 2.0F,
                        this.posZ + z,
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }
    }
    
    
    // ==================================================
  	//                 Summon Creatures
  	// ==================================================
    public boolean summonCreatures() {
    	if(this.getEntityWorld().isRemote)
    		return true;
        if(this.summonClass == null)
            return false;
    	for(int i = 0; i < this.summonAmount; i++) {
	    	Entity entity = null;
			try {
				entity = (Entity)this.summonClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.getEntityWorld()});
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "A none Entity class type was passed to an EntityPortal, only entities can be summoned from portals!");
				e.printStackTrace();
			}
	    	if(entity == null)
	    		return false;
	    	entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
	    	if(entity instanceof EntityCreatureBase) {
                EntityCreatureBase entityCreature = (EntityCreatureBase) entity;

                // Summoning Staff:
                if (this.shootingEntity != null && this.summoningPedestal == null) {
                    entityCreature.setMinion(true);
                    if (entityCreature instanceof EntityCreatureTameable) {
                        ((EntityCreatureTameable) entityCreature).setPlayerOwner(this.shootingEntity);
                        if (this.portalItem != null) {
                            this.portalItem.applyMinionBehaviour((EntityCreatureTameable) entityCreature, this.shootingEntity);
                            this.portalItem.applyMinionEffects(entityCreature);
                        }
                    }
                }

                // Summoning Pedestal:
                else if (this.summoningPedestal != null && this.summoningPedestal.getOwnerUUID() != null) {
                    entityCreature.setMinion(true);
                    if (entityCreature instanceof EntityCreatureTameable) {
                        ((EntityCreatureTameable) entityCreature).setPlayerOwner(this.summoningPedestal.getOwnerUUID(), this.summoningPedestal.getOwnerName());
                        this.summoningPedestal.applyMinionBehaviour((EntityCreatureTameable) entityCreature);
                    }
                }

                if (this.summonDuration > 0)
                    entityCreature.setTemporary(this.summonDuration);

                if (this.shootingEntity != null)
                    this.shootingEntity.addStat(ObjectManager.getAchievement(entityCreature.mobInfo.name + ".summon"), 1);
            }
	    	this.getEntityWorld().spawnEntity(entity);
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
    	if(this.shootingEntity != null && this.summoningPedestal == null) {
    		// Get Look Target
	        Vec3d lookDirection = this.shootingEntity.getLookVec();
			this.targetX = this.shootingEntity.posX + (lookDirection.xCoord * this.portalRange);
			this.targetY = this.shootingEntity.posY + this.shootingEntity.getEyeHeight() + (lookDirection.yCoord * this.portalRange);
			this.targetZ = this.shootingEntity.posZ + (lookDirection.zCoord * this.portalRange);
	        
			// Apply Raytrace to Look Target:
			RayTraceResult target = Utilities.raytrace(this.getEntityWorld(), this.shootingEntity.posX, this.shootingEntity.posY + this.shootingEntity.getEyeHeight(), this.shootingEntity.posZ, this.targetX, this.targetY, this.targetZ, 1.0F, null);
	        if(target != null && target.hitVec != null) {
				this.targetX = target.hitVec.xCoord;
				this.targetY = target.hitVec.yCoord;
				this.targetZ = target.hitVec.zCoord;
	        }
	        
	        this.targetY += 1.0D;
			
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
    protected void onImpact(RayTraceResult movingObjectPos) {}
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName) == null)
     		AssetManager.addTexture(this.entityName, LycanitesMobs.group, "textures/particles/" + this.entityName.toLowerCase() + ".png");
        if(AssetManager.getTexture(this.entityName + "_client") == null)
            AssetManager.addTexture(this.entityName + "_client", LycanitesMobs.group, "textures/particles/" + this.entityName.toLowerCase() + "_client.png");
        if(AssetManager.getTexture(this.entityName + "_player") == null)
            AssetManager.addTexture(this.entityName + "_player", LycanitesMobs.group, "textures/particles/" + this.entityName.toLowerCase() + "_player.png");

        if(this.ownerName != null) {
            if(this.ownerName.equalsIgnoreCase(LycanitesMobs.proxy.getClientPlayer().getName()))
                return AssetManager.getTexture(this.entityName + "_client");
            if(!this.ownerName.equalsIgnoreCase(""))
                return AssetManager.getTexture(this.entityName + "_player");
        }
     	return AssetManager.getTexture(this.entityName);
    }

    @Override
    public float getTextureOffsetY() {
        return this.height / 2;
    }
}
