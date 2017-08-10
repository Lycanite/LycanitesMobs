package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.ExtendedPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ExtendedPlayerStorage implements Capability.IStorage<IExtendedPlayer> {

    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    @Override
    public void readNBT(Capability<IExtendedPlayer> capability, IExtendedPlayer instance, EnumFacing facing, NBTBase nbt) {
        if(!(instance instanceof ExtendedPlayer) || !(nbt instanceof NBTTagCompound))
            return;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)instance;
        NBTTagCompound extTagCompound = (NBTTagCompound)nbt;
        extendedPlayer.readNBT(extTagCompound);
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    @Override
    public NBTBase writeNBT(Capability<IExtendedPlayer> capability, IExtendedPlayer instance, EnumFacing facing) {
        if(!(instance instanceof ExtendedPlayer))
            return null;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)instance;
        NBTTagCompound extTagCompound = new NBTTagCompound();
        extendedPlayer.writeNBT(extTagCompound);
        return extTagCompound;
    }
}
