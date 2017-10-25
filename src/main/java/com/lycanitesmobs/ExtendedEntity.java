package com.lycanitesmobs;

import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Vector3d;
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
    Vector3d lastSafePos;
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
        if(this.entity.getEntityWorld() != null) {
            if (this.lastSafePos == null) {
                this.lastSafePos = new Vector3d(this.entity.posX, this.entity.posY, this.entity.posZ);
            }
            if (!this.entity.getEntityWorld().getBlockState(this.entity.getPosition()).getMaterial().isSolid()) {
                this.lastSafePos.set(Math.floor(this.entity.posX) + 0.5D, this.entity.getPosition().getY(), Math.floor(this.entity.posZ) + 0.5D);
            }
        }

        // Fear Entity:
        if (this.fearEntity != null && !this.fearEntity.isEntityAlive())
            this.fearEntity = null;

        // Picked Up By Entity:
		try {
			this.updatePickedUpByEntity();
		}
		catch (Exception e) {}
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
        if(this.pickedUpByEntity == null || this.entity.getEntityWorld() == null)
            return;

        // Check:
        if(!this.pickedUpByEntity.isEntityAlive()) {
            this.setPickedUpByEntity(null);
            return;
        }
        if(this.pickedUpByEntity instanceof EntityLivingBase) {
            if(((EntityLivingBase)this.pickedUpByEntity).getHealth() <= 0) {
                this.setPickedUpByEntity(null);
                return;
            }
        }
        if(ObjectManager.getPotionEffect("weight") != null) {
            if(this.entity.isPotionActive(ObjectManager.getPotionEffect("weight"))) {
                this.setPickedUpByEntity(null);
                return;
            }
        }
        if(this.entity.getDistanceSqToEntity(this.pickedUpByEntity) > 32D) {
            this.setPickedUpByEntity(null);
            return;
        }

        // Movement:
        double[] pickupOffset = this.getPickedUpOffset();
        this.entity.setPosition(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
        this.entity.motionX = this.pickedUpByEntity.motionX;
        this.entity.motionY = this.pickedUpByEntity.motionY;
        this.entity.motionZ = this.pickedUpByEntity.motionZ;
        this.entity.fallDistance = 0;
        if (!this.entity.getEntityWorld().isRemote && this.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.entity;
            player.capabilities.allowFlying = true;
            this.entity.noClip = true;
        }
        if (!this.entity.isEntityAlive())
            this.setPickedUpByEntity(null);
        if (this.entity instanceof EntityLivingBase) {
            if (this.entity.getHealth() <= 0)
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
                    this.entity.noClip = false;
                }
			}

            // Teleport To Initial Pickup Position:
            if(this.pickedUpByEntity != null && !(this.entity instanceof EntityPlayer)) {
                double[] pickupOffset = this.getPickedUpOffset();
                this.entity.attemptTeleport(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
            }

			MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
			LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
		}

        // Safe Drop Position:
        if(pickedUpByEntity == null) {
            if(this.lastSafePos != null) {
                this.entity.setPosition(this.lastSafePos.getX(), this.lastSafePos.getY(), this.lastSafePos.getZ());
            }
            this.entity.motionX = 0;
            this.entity.motionY = 0;
            this.entity.motionZ = 0;
            this.entity.fallDistance = 0;
        }
	}

	public double[] getPickedUpOffset() {
        double[] pickupOffset = new double[] {0, 0, 0};
        if(this.pickedUpByEntity instanceof EntityCreatureBase) {
            pickupOffset = ((EntityCreatureBase) this.pickedUpByEntity).getPickupOffset(this.entity);
        }
        if(MobInfo.disablePickupOffsets && this.entity instanceof EntityPlayer) {
            return new double[] {0, 0, 0};
        }
        return pickupOffset;
    }

    public boolean isPickedUp() {
        return this.pickedUpByEntity != null;
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
