package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.core.entity.EntityPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityBase extends TileEntity implements ITickable {
    public EntityPortal summoningPortal;

    // ========================================
    //                  Remove
    // ========================================
    /** Can be called by a block when broken to alert this TileEntity that it is being removed. **/
    public void onRemove() {}


    // ========================================
    //                  Update
    // ========================================
    /** The main update called every tick. **/
    @Override
    public void update() {}


    // ========================================
    //              Client Events
    // ========================================
    @Override
    public boolean receiveClientEvent(int eventID, int eventArg) {
        return false;
    }


    // ========================================
    //             Network Packets
    // ========================================
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {}


    // ========================================
    //                 NBT Data
    // ========================================
    /** Reads from saved NBT data. **/
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
    }

    /** Writes to NBT data. **/
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        return super.writeToNBT(nbtTagCompound);
    }


    // ========================================
    //                Open GUI
    // ========================================
    /** Called by the GUI Handler when opening a GUI. **/
    public Object getGUI(EntityPlayer player) {
        return null;
    }
}
