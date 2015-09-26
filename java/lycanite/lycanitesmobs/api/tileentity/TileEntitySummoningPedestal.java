package lycanite.lycanitesmobs.api.tileentity;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.container.ContainerBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GUISummoningPedestal;
import lycanite.lycanitesmobs.api.network.MessageSummoningPedestal;
import lycanite.lycanitesmobs.api.network.PacketHandler;
import lycanite.lycanitesmobs.api.pets.SummonSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

import java.util.UUID;

public class TileEntitySummoningPedestal extends TileEntityBase {

    // Summoning Properties:
    public EntityPortal summoningPortal;
    public UUID ownerUUID;
    public String ownerName = "";
    public SummonSet summonSet;
    public int summonAmount = 1;

    // Summoning Stats:
    public int capacityCharge = 100;
    public int capacity = 0;
    public int capacityMax = (this.capacityCharge * 10);
    public int summonProgress = 0;
    public int summonProgressMax = 3 * 60;


    // ========================================
    //                  Remove
    // ========================================
    /** Can be called by a block when broken to alert this TileEntity that it is being removed. **/
    @Override
    public void onRemove() {
        if(this.summoningPortal != null && !this.summoningPortal.isDead) {
            this.summoningPortal.setDead();
        }
    }


    // ========================================
    //                  Update
    // ========================================
    /** The main update called every tick. **/
    @Override
    public void updateEntity() {
        if(this.worldObj.isRemote || this.summonSet == null)
            return;

        if(this.summonSet.getFollowing())
            this.summonSet.following = false;

        // Summoning Portal:
        if(this.summoningPortal == null || this.summoningPortal.isDead) {
            this.summoningPortal = new EntityPortal(this.worldObj, this);
            this.summoningPortal.setProjectileScale(8);
            this.worldObj.spawnEntityInWorld(this.summoningPortal);
        }

        // Check Capacity:
        if(this.capacity-- + this.summonSet.getMobInfo().summonCost > this.capacityMax) {
            this.summonProgress = 0;
        }

        // Summon Minions:
        if(this.summonProgress++ >= this.summonProgressMax) {
            this.summoningPortal.summonCreatures();
            this.summonProgress = 0;
            this.capacity = Math.min(this.capacity + (this.capacityCharge * this.summonSet.getMobInfo().summonCost), this.capacityMax);
        }
    }


    // ========================================
    //           Summoning Pedestal
    // ========================================
    /** Sets the owner of this block. **/
    public void setOwner(EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            this.ownerUUID = entity.getUniqueID();
            this.ownerName = entity.getCommandSenderName();
        }
    }

    /** Returns the name of the owner of this pedestal. **/
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    /** Returns the name of the owner of this pedestal. **/
    public String getOwnerName() {
        return this.ownerName;
    }

    /** Returns the player that this belongs to or null if owned by no player. **/
    public EntityPlayer getPlayer() {
        if(this.ownerUUID == null)
            return null;
        return this.worldObj.func_152378_a(this.ownerUUID); // getPlayerEntityByUUID
    }

    /** Returns the class that this is summoning. **/
    public Class getSummonClass() {
        return this.summonSet.getCreatureClass();
    }

    /** Sets the Summon Set for this to use. **/
    public void setSummonSet(SummonSet summonSet) {
        this.summonSet = new SummonSet(null);
        this.summonSet.setSummonType(summonSet.summonType);
        this.summonSet.sitting = summonSet.getSitting();
        this.summonSet.following = false;
        this.summonSet.passive = summonSet.getPassive();
        this.summonSet.aggressive = summonSet.getAggressive();
        this.summonSet.pvp = summonSet.getPVP();
    }


    // ========== Minion Behaviour ==========
    /** Applies the minion behaviour to the summoned player owned minion. **/
    public void applyMinionBehaviour(EntityCreatureTameable minion) {
        if(this.summonSet != null)
            this.summonSet.applyBehaviour(minion);
    }


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
    public Packet getDescriptionPacket() {
        NBTTagCompound syncData = new NBTTagCompound();

        // Both:
        if(this.summonSet != null) {
            NBTTagCompound summonSetNBT = new NBTTagCompound();
            this.summonSet.writeToNBT(summonSetNBT);
            syncData.setTag("SummonSet", summonSetNBT);
        }

        // Server to Client:
        if(!this.worldObj.isRemote) {
            syncData.setString("OwnerUUID", this.getOwnerUUID().toString());
            syncData.setString("OwnerName", this.getOwnerName());
        }

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        if(!this.worldObj.isRemote)
            return;

        NBTTagCompound syncData = packet.func_148857_g(); // Get NBT from Packet.
        if (syncData.hasKey("OwnerUUID"))
            this.ownerUUID = UUID.fromString(syncData.getString("OwnerUUID"));
        if (syncData.hasKey("OwnerName"))
            this.ownerName = syncData.getString("OwnerName");
        if (syncData.hasKey("SummonSet")) {
            SummonSet summonSet = new SummonSet(null);
            summonSet.readFromNBT(syncData.getCompoundTag("SummonSet"));
            this.summonSet = summonSet;
        }
    }

    public void sendSummonSetToServer(SummonSet summonSet) {
        LycanitesMobs.packetHandler.sendToServer(new MessageSummoningPedestal(summonSet, this.xCoord, this.yCoord, this.zCoord));
    }


    // ========================================
    //                 NBT Data
    // ========================================
    /** Reads from saved NBT data. **/
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        if(nbtTagCompound.hasKey("OwnerUUID")) {
            String uuidString = nbtTagCompound.getString("OwnerUUID");
            if(!"".equals(uuidString))
                this.ownerUUID = UUID.fromString(uuidString);
            else
                this.ownerUUID = null;
        }
        else {
            this.ownerUUID = null;
        }

        if(nbtTagCompound.hasKey("OwnerName")) {
            this.ownerName = nbtTagCompound.getString("OwnerName");
        }
        else {
            this.ownerName = "";
        }

        if(nbtTagCompound.hasKey("SummonSet")) {
            NBTTagCompound summonSetNBT = nbtTagCompound.getCompoundTag("SummonSet");
            SummonSet summonSet = new SummonSet(null);
            summonSet.readFromNBT(summonSetNBT);
            this.summonSet = summonSet;
        }
        else
            this.summonSet = null;
    }

    /** Writes to NBT data. **/
    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        if(this.ownerUUID == null) {
            nbtTagCompound.setString("OwnerUUID", "");
        }
        else {
            nbtTagCompound.setString("OwnerUUID", this.ownerUUID.toString());
        }

        if(this.summonSet != null) {
            NBTTagCompound summonSetNBT = new NBTTagCompound();
            this.summonSet.writeToNBT(summonSetNBT);
            nbtTagCompound.setTag("SummonSet", summonSetNBT);
        }


        nbtTagCompound.setString("OwnerName", this.ownerName);
    }


    // ========================================
    //                Open GUI
    // ========================================
    @Override
    public Object getGUI(EntityPlayer player) {
        if(this.worldObj.isRemote)
            return new GUISummoningPedestal(player, this);
        return new ContainerBase(this);
    }
}
