package lycanite.lycanitesmobs.core.capabilities;

import lycanite.lycanitesmobs.ExtendedEntity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ExtendedEntityStorage implements Capability.IStorage<IExtendedEntity> {

    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    @Override
    public void readNBT(Capability<IExtendedEntity> capability, IExtendedEntity instance, EnumFacing facing, NBTBase nbt) {
        if(!(instance instanceof ExtendedEntity) || !(nbt instanceof NBTTagCompound))
            return;
        ExtendedEntity extendedEntity = (ExtendedEntity)instance;
        NBTTagCompound extTagCompound = (NBTTagCompound)nbt;
        extendedEntity.readNBT(extTagCompound);
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    @Override
    public NBTBase writeNBT(Capability<IExtendedEntity> capability, IExtendedEntity instance, EnumFacing facing) {
        if(!(instance instanceof ExtendedEntity))
            return null;
        ExtendedEntity extendedEntity = (ExtendedEntity)instance;
        NBTTagCompound extTagCompound = new NBTTagCompound();
        extendedEntity.writeNBT(extTagCompound);
        return extTagCompound;
    }
}