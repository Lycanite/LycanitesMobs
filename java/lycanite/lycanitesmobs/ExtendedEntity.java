package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedEntity implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsEntity";
	
	public Entity entity;
	
	// States:
	public Entity pickedUpByEntity;
	private int pickedUpByEntityID;
	
	private boolean playerFlyingSnapshot;
	
	public EntityFear fearEntity;
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(Entity entity) {
		if(entity == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedEntity from a null Entity.");
			return null;
		}
		IExtendedEntityProperties entityIExt = entity.getExtendedProperties(EXT_PROP_NAME);
		ExtendedEntity entityExt;
		if(entityIExt != null)
			entityExt = (ExtendedEntity)entityIExt;
		else
			entityExt = new ExtendedEntity(entity);
		
		return entityExt;
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedEntity(Entity entity) {
		entity.registerExtendedProperties(ExtendedEntity.EXT_PROP_NAME, this);
	}
	
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		this.entity = entity;
	}
	
	
	// ==================================================
    //                      Update
    // ==================================================
	public void update() {
		// Picked Up By Entity:
		if(this.pickedUpByEntity != null) {
			if(!this.pickedUpByEntity.isEntityAlive())
				this.setPickedUpByEntity(null);
			else if(this.pickedUpByEntity instanceof EntityLivingBase) {
				if(((EntityLivingBase)this.pickedUpByEntity).getHealth() <= 0)
					this.setPickedUpByEntity(null);
			}
			else if(this.entity.getDistanceSqToEntity(this.pickedUpByEntity) > 32D) {
				this.setPickedUpByEntity(null);
			}
		}
		
		if(this.pickedUpByEntity != null) {
			double[] pickupOffset = new double[]{0, 0, 0};
			if(this.pickedUpByEntity instanceof EntityCreatureBase)
				pickupOffset = ((EntityCreatureBase)this.pickedUpByEntity).getPickupOffset(this.entity);
			double yPos = this.pickedUpByEntity.posY;
			if(this.entity.worldObj.isRemote && entity instanceof EntityPlayer) {
				yPos = this.pickedUpByEntity.boundingBox.minY + entity.height;
			}
			this.entity.setPosition(this.pickedUpByEntity.posX + pickupOffset[0], yPos + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
			//this.entity.setVelocity(this.pickedUpByEntity.motionX, this.pickedUpByEntity.motionY, this.pickedUpByEntity.motionZ); Not valid server side I think. :/
			this.entity.motionX = this.pickedUpByEntity.motionX;
			this.entity.motionY = this.pickedUpByEntity.motionY;
			this.entity.motionZ = this.pickedUpByEntity.motionZ;
			this.entity.fallDistance = 0;
			if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = true;
			}
			if(!this.entity.isEntityAlive())
				this.setPickedUpByEntity(null);
			if(this.entity instanceof EntityLivingBase) {
				if(((EntityLivingBase)this.entity).getHealth() <= 0)
					this.setPickedUpByEntity(null);
			}
    	}
		else if(this.pickedUpByEntityID != (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0)) {
			if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = this.playerFlyingSnapshot;
			}
		}
		this.pickedUpByEntityID = (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0);
		
		// Fear Entity:
		if(this.fearEntity != null && !this.fearEntity.isEntityAlive())
			this.fearEntity = null;
	}
	
	
	// ==================================================
    //                 Death
    // ==================================================
	public void onDeath() {
		this.setPickedUpByEntity(null);
	}
	
	
	// ==================================================
    //                 Picked Up By Entity
    // ==================================================
	public void setPickedUpByEntity(Entity pickedUpByEntity) {
		if(this.entity.ridingEntity != null)
			this.entity.mountEntity(null);
		if(this.entity.riddenByEntity != null)
			this.entity.riddenByEntity.mountEntity(null);
		this.pickedUpByEntity = pickedUpByEntity;
		if(!this.entity.worldObj.isRemote) {
			if(this.entity instanceof EntityPlayer) {
				if(pickedUpByEntity != null)
					this.playerFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.allowFlying;
				else
					((EntityPlayer)this.entity).capabilities.allowFlying = this.playerFlyingSnapshot;
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
	@Override
    public void loadNBTData(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("ActiveEffects", 9))  {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("ActiveEffects", 9);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
                byte potionID = nbttagcompound1.getByte("Id");
                if(potionID >= Potion.potionTypes.length || Potion.potionTypes[potionID] == null) {
                	nbttaglist.removeTag(i);
    				LycanitesMobs.printWarning("EffectsSetup", "Found a null potion effect in entity NBTTag, this effect has been removed.");
                }
            }
        }
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
	@Override
    public void saveNBTData(NBTTagCompound nbtTagCompound) {
		
    }
}
