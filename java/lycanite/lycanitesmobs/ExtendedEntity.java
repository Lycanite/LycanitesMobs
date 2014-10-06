package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.network.MessageEntityPickedUp;
import net.minecraft.entity.Entity;
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
			this.entity.setPosition(this.pickedUpByEntity.posX, this.pickedUpByEntity.posY, this.pickedUpByEntity.posZ);
			//this.entity.setVelocity(this.pickedUpByEntity.motionX, this.pickedUpByEntity.motionY, this.pickedUpByEntity.motionZ);
			this.entity.motionX = this.pickedUpByEntity.motionX;
			this.entity.motionY = this.pickedUpByEntity.motionY;
			this.entity.motionZ = this.pickedUpByEntity.motionZ;
			this.entity.fallDistance = 0;
			if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = true;
			}
			if(!this.pickedUpByEntity.isEntityAlive())
				this.setPickedUpByEntity(null);
    	}
		else if(this.pickedUpByEntityID != (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0)) {
			if(!this.entity.worldObj.isRemote && this.entity instanceof EntityPlayer) {
				((EntityPlayer)this.entity).capabilities.allowFlying = this.playerFlyingSnapshot;
			}
		}
		this.pickedUpByEntityID = (this.pickedUpByEntity != null ? this.pickedUpByEntity.getEntityId() : 0);
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
