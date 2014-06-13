package lycanite.lycanitesmobs;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedEntity implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsEntity";
	
	// ==================================================
    //                   Get for Entity
    // ==================================================
	public static ExtendedEntity getForEntity(Entity entity) {
		if(entity == null) {
			LycanitesMobs.printWarning("", "Tried to access an ExtendedEntity from a null Entity.");
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
