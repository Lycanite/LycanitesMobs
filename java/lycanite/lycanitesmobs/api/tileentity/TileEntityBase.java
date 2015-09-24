package lycanite.lycanitesmobs.api.tileentity;

import lycanite.lycanitesmobs.api.entity.EntityPortal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBase extends TileEntity {
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
    public void updateEntity() {}


    // ========================================
    //              Client Events
    // ========================================
    public boolean receiveClientEvent(int eventID, int eventArg) {
        return false;
    }


    // ========================================
    //             Network Packets
    // ========================================
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {}


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
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
    }
}
