package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class ExtendedEntity {
    public static Map<Entity, ExtendedEntity> extendedEntities = new HashMap<Entity, ExtendedEntity>();
    public static String[] FORCE_REMOVE_ENTITY_IDS;
    public static int FORCE_REMOVE_ENTITY_TICKS = 40;
	
	public Entity entity;
	
	// States:
	public Entity pickedUpByEntity;
	private int pickedUpByEntityID;

    /** The last coordinates the entity was at where it wasn't inside an opaque block. (Helps prevent suffocation). **/
    double[] lastSafePos;
    private boolean playerAllowFlyingSnapshot;
    private boolean playerIsFlyingSnapshot;
	
	public EntityFear fearEntity;

    // Force Remove:
    boolean forceRemoveChecked = false;
    boolean forceRemove = false;
    int forceRemoveTicks = FORCE_REMOVE_ENTITY_TICKS;
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(Entity entity) {
		if(entity == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}

		if(extendedEntities.containsKey(entity))
            return extendedEntities.get(entity);

		return new ExtendedEntity(entity);
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedEntity(Entity entity) {
        this.entity = entity;
		extendedEntities.put(entity, this);
	}
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
        if(this.entity == null)
            return;

        try {
            // Force Remove Entity:
            if (this.entity.worldObj != null && !this.entity.worldObj.isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.length > 0 && !this.forceRemoveChecked) {
                LycanitesMobs.printDebug("ForceRemoveEntity", "Forced entity removal, checking: " + this.entity.getName());
                for (String forceRemoveID : FORCE_REMOVE_ENTITY_IDS) {
                    if (forceRemoveID.equalsIgnoreCase(this.entity.getName())) {
                        this.forceRemove = true;
                        break;
                    }
                }
                this.forceRemoveChecked = true;
            }
            if (this.forceRemove && this.forceRemoveTicks-- <= 0)
                this.entity.setDead();

            // Safe Position:
            if (this.lastSafePos == null) {
                this.lastSafePos = new double[]{this.entity.posX, this.entity.posY, this.entity.posZ};
            }
            if (this.entity.noClip || (!this.entity.isEntityInsideOpaqueBlock())) {
                this.lastSafePos[0] = this.entity.posX;
                this.lastSafePos[1] = this.entity.posY;
                this.lastSafePos[2] = this.entity.posZ;
            }

            // Fear Entity:
            if (this.fearEntity != null && !this.fearEntity.isEntityAlive())
                this.fearEntity = null;
        }
        catch(Exception e) {}
	}
	
	
	// ==================================================
    //                       Death
    // ==================================================
	public void onDeath() {
		this.setPickedUpByEntity(null);
	}
	
	
	// ==================================================
    //                 Picked Up By Entity
    // ==================================================
    public void updatePickedUpByEntity() {
        Entity pickedUpByEntityInstance = this.pickedUpByEntity;
        if(pickedUpByEntityInstance == null || this.entity.getEntityWorld() == null)
            return;

        // Check:
        if(!pickedUpByEntityInstance.isEntityAlive()) {
            this.setPickedUpByEntity(null);
            return;
        }
        if(pickedUpByEntityInstance instanceof EntityLivingBase) {
            if(((EntityLivingBase)pickedUpByEntityInstance).getHealth() <= 0) {
                this.setPickedUpByEntity(null);
                return;
            }
        }
        if(this.entity instanceof EntityLivingBase && ObjectManager.getPotionEffect("weight") != null) {
            if(((EntityLivingBase)(this.entity)).isPotionActive(ObjectManager.getPotionEffect("weight"))) {
                this.setPickedUpByEntity(null);
                return;
            }
        }
        if(this.entity.getDistanceSqToEntity(pickedUpByEntityInstance) > 32D) {
            this.setPickedUpByEntity(null);
            return;
        }

        // Movement:
        double[] pickupOffset = new double[]{0, 0, 0};
        if(pickedUpByEntityInstance instanceof EntityCreatureBase)
            pickupOffset = ((EntityCreatureBase)pickedUpByEntityInstance).getPickupOffset(this.entity);
        double yPos = pickedUpByEntityInstance.posY;
        if(this.entity.worldObj.isRemote && entity instanceof EntityPlayer) {
            yPos = pickedUpByEntityInstance.getEntityBoundingBox().minY + entity.height;
        }
        this.entity.setPosition(pickedUpByEntityInstance.posX + pickupOffset[0], yPos + pickupOffset[1], pickedUpByEntityInstance.posZ + pickupOffset[2]);
        this.entity.motionX = pickedUpByEntityInstance.motionX;
        this.entity.motionY = pickedUpByEntityInstance.motionY;
        this.entity.motionZ = pickedUpByEntityInstance.motionZ;
        this.entity.fallDistance = 0;
        if (!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
            ((EntityPlayer) this.entity).capabilities.allowFlying = true;
        }
        if (!this.entity.isEntityAlive())
            this.setPickedUpByEntity(null);
        if (this.entity instanceof EntityLivingBase) {
            if (((EntityLivingBase) this.entity).getHealth() <= 0)
                this.setPickedUpByEntity(null);
        }
    }

	public void setPickedUpByEntity(Entity pickedUpByEntity) {
        if(this.pickedUpByEntity == pickedUpByEntity || this.entity == null)
            return;

		if(this.entity.getRidingEntity() != null)
			this.entity.dismountRidingEntity();
        // No longer dismount passengers.
		this.pickedUpByEntity = pickedUpByEntity;

        // Server Side:
		if(!this.entity.worldObj.isRemote) {

            // Player Flying:
			if(this.entity instanceof EntityPlayer) {
				if(pickedUpByEntity != null) {
                    this.playerAllowFlyingSnapshot = ((EntityPlayer) this.entity).capabilities.allowFlying;
                    this.playerIsFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.isFlying;
                }
				else {
                    ((EntityPlayer)this.entity).capabilities.allowFlying = this.playerAllowFlyingSnapshot;
                    ((EntityPlayer)this.entity).capabilities.isFlying = this.playerIsFlyingSnapshot;
                }
			}

            // Safe Position:
            if(pickedUpByEntity == null) {
                if(this.lastSafePos != null && this.lastSafePos.length >= 3)
                    this.entity.setPosition(this.lastSafePos[0], this.lastSafePos[1], this.lastSafePos[2]);
                this.entity.motionX = 0;
                this.entity.motionY = 0;
                this.entity.motionZ = 0;
                this.entity.fallDistance = 0;
            }

			MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
			LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
		}
	}
	
	public boolean isFeared() {
		return this.pickedUpByEntity instanceof EntityFear;
	}
}
