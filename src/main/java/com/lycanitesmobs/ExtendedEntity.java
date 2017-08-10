package com.lycanitesmobs;

import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class ExtendedEntity implements IExtendedEntity {
    public static Map<Entity, ExtendedEntity> clientExtendedEntities = new HashMap<Entity, ExtendedEntity>();
    public static String[] FORCE_REMOVE_ENTITY_IDS;
    public static int FORCE_REMOVE_ENTITY_TICKS = 40;

    // Entity Instance:
	public EntityLivingBase entity;

    // Safe Position:
    /** The last coordinates the entity was at where it wasn't inside an opaque block. (Helps prevent suffocation). **/
    double[] lastSafePos;
    private boolean playerAllowFlyingSnapshot;
    private boolean playerIsFlyingSnapshot;
	
	// Picked Up:
	public Entity pickedUpByEntity;
	private int pickedUpByEntityID;

    // Fear:
	public EntityFear fearEntity;

    // Force Remove:
    boolean forceRemoveChecked = false;
    boolean forceRemove = false;
    int forceRemoveTicks = FORCE_REMOVE_ENTITY_TICKS;
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(EntityLivingBase entity) {
		if(entity == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}

        // Client Side:
        if(entity.getEntityWorld() != null && entity.getEntityWorld().isRemote) {
            if(clientExtendedEntities.containsKey(entity)) {
                ExtendedEntity extendedEntity = clientExtendedEntities.get(entity);
                extendedEntity.setEntity(entity);
                return extendedEntity;
            }
            ExtendedEntity extendedEntity = new ExtendedEntity();
            extendedEntity.setEntity(entity);
            clientExtendedEntities.put(entity, extendedEntity);
        }

        // Server Side:
        IExtendedEntity iExtendedEntity = null;
        try {
            iExtendedEntity = entity.getCapability(LycanitesMobs.EXTENDED_ENTITY, null);
        }
        catch(Exception e) {}
        if(iExtendedEntity == null || !(iExtendedEntity instanceof ExtendedEntity))
            return null;
        ExtendedEntity extendedEntity = (ExtendedEntity)iExtendedEntity;
        if(extendedEntity.getEntity() != entity)
            extendedEntity.setEntity(entity);
        return extendedEntity;
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedEntity() {

	}


    // ==================================================
    //                      Entity
    // ==================================================
    /** Initially sets the entity. **/
    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
        if(this.entity == null)
            return;

        // Force Remove Entity:
        if (this.entity.getEntityWorld() != null && !this.entity.getEntityWorld().isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.length > 0 && !this.forceRemoveChecked) {
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

        // Picked Up By Entity:
        this.updatePickedUpByEntity();
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
        if(this.entity.getEntityWorld().isRemote && entity instanceof EntityPlayer) {
            yPos = pickedUpByEntityInstance.getEntityBoundingBox().minY + entity.height;
        }
        this.entity.setPosition(pickedUpByEntityInstance.posX + pickupOffset[0], yPos + pickupOffset[1], pickedUpByEntityInstance.posZ + pickupOffset[2]);
        this.entity.motionX = pickedUpByEntityInstance.motionX;
        this.entity.motionY = pickedUpByEntityInstance.motionY;
        this.entity.motionZ = pickedUpByEntityInstance.motionZ;
        this.entity.fallDistance = 0;
        if (!this.entity.getEntityWorld().isRemote && this.entity instanceof EntityPlayer) {
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
		if(!this.entity.getEntityWorld().isRemote) {

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


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readNBT(NBTTagCompound nbtTagCompound) {

    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeNBT(NBTTagCompound nbtTagCompound) {

    }
}
