package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.container.ContainerBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.network.MessageSummoningPedestalStats;
import com.lycanitesmobs.core.network.MessageSummoningPedestalSummonSet;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.GUISummoningPedestal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TileEntitySummoningPedestal extends TileEntityBase {

    public long updateTick = 0;

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

    // Summoned Minions:
    public List<EntityCreatureBase> minions = new ArrayList<EntityCreatureBase>();
    protected String[] loadMinionIDs;

    // Block:
    protected boolean blockStateSet = false;


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
    public void update() {
        // Client Side Only:
        if(this.worldObj.isRemote) {
            if(this.summonProgress >= this.summonProgressMax)
                this.summonProgress = 0;
            else if(this.summonProgress > 0)
                this.summonProgress++;
            return;
        }

        // Load Minion IDs:
        if(this.loadMinionIDs != null) {
            int range = 20;
            List nearbyEntities = this.worldObj.getEntitiesWithinAABB(EntityCreatureBase.class,
                    new AxisAlignedBB(this.getPos().getX() - range, this.getPos().getY() - range, this.getPos().getZ() - range,
                            this.getPos().getX() + range, this.getPos().getY() + range, this.getPos().getZ() + range));
            Iterator possibleEntities = nearbyEntities.iterator();
            while(possibleEntities.hasNext()) {
                EntityCreatureBase possibleEntity = (EntityCreatureBase)possibleEntities.next();
                for(String loadMinionID : this.loadMinionIDs) {
                    UUID uuid = null;
                    try { uuid = UUID.fromString(loadMinionID); } catch (Exception e) {}
                    if(possibleEntity.getUniqueID().equals(uuid)) {
                        this.minions.add(possibleEntity);
                        break;
                    }
                }
            }
            this.loadMinionIDs = null;
        }

        if(this.summonSet == null || this.summonSet.getMobInfo() == null)
            return;

        if(this.summonSet.getFollowing())
            this.summonSet.following = false;

        // Summoning Portal:
        if(this.summoningPortal == null || this.summoningPortal.isDead) {
            this.summoningPortal = new EntityPortal(this.worldObj, this);
            this.summoningPortal.setProjectileScale(8);
            this.worldObj.spawnEntityInWorld(this.summoningPortal);
        }

        // Update Minions:
        if(this.updateTick % 100 == 0) {
            this.capacity = 0;
            for (EntityCreatureBase minion : this.minions.toArray(new EntityCreatureBase[this.minions.size()])) {
                if(minion == null || minion.isDead)
                    this.minions.remove(minion);
                else {
                    this.capacity += (minion.mobInfo.summonCost * this.capacityCharge);
                }
            }
        }

        // Check Capacity:
        if(this.capacity + this.summonSet.getMobInfo().summonCost > this.capacityMax) {
            this.summonProgress = 0;
        }

        // Summon Minions:
        else if(this.summonProgress++ >= this.summonProgressMax) {
            this.summoningPortal.summonCreatures();
            this.summonProgress = 0;
            this.capacity = Math.min(this.capacity + (this.capacityCharge * this.summonSet.getMobInfo().summonCost), this.capacityMax);
        }

        // Block State:
        if(!this.blockStateSet) {
            if(!"".equals(this.getOwnerName()))
                BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.PLAYER, this.getWorld(), this.getPos());
            else
                BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.NONE, this.getWorld(), this.getPos());
            this.blockStateSet = true;
        }

        // Sync To Client:
        if(this.updateTick % 20 == 0) {
            LycanitesMobs.packetHandler.sendToAllAround(new MessageSummoningPedestalStats(this.capacity, this.summonProgress, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()),
                    new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimension(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 5D));
        }

        this.updateTick++;
    }


    // ========================================
    //           Summoning Pedestal
    // ========================================
    /** Sets the owner of this block. **/
    public void setOwner(EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            this.ownerUUID = entity.getUniqueID();
            this.ownerName = entity.getName();
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
        return this.worldObj.getPlayerEntityByUUID(this.ownerUUID);
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
        this.minions.add(minion);
        minion.setHome(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 20);
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound syncData = new NBTTagCompound();

        // Both:
        if(this.summonSet != null) {
            NBTTagCompound summonSetNBT = new NBTTagCompound();
            this.summonSet.writeToNBT(summonSetNBT);
            syncData.setTag("SummonSet", summonSetNBT);
        }

        // Server to Client:
        if(!this.worldObj.isRemote && this.getOwnerUUID() != null && this.getOwnerName() != null) {
            syncData.setString("OwnerUUID", this.getOwnerUUID().toString());
            syncData.setString("OwnerName", this.getOwnerName());
        }

        return new SPacketUpdateTileEntity(this.getPos(), 1, syncData);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if(!this.worldObj.isRemote)
            return;

        NBTTagCompound syncData = packet.getNbtCompound();
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
        LycanitesMobs.packetHandler.sendToServer(new MessageSummoningPedestalSummonSet(summonSet, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
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

        if(nbtTagCompound.hasKey("MinionIDs")) {
            NBTTagList minionIDs = nbtTagCompound.getTagList("MinionIDs", 10);
            this.loadMinionIDs = new String[minionIDs.tagCount()];
            for(int i = 0; i < minionIDs.tagCount(); i++) {
                NBTTagCompound minionID = minionIDs.getCompoundTagAt(i);
                if(minionID.hasKey("ID")) {
                    this.loadMinionIDs[i] = minionID.getString("ID");
                }
            }
        }
    }

    /** Writes to NBT data. **/
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
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

        if(this.minions.size() > 0) {
            NBTTagList minionIDs = new NBTTagList();
            for(EntityLivingBase minion : this.minions) {
                NBTTagCompound minionID = new NBTTagCompound();
                minionID.setString("ID", minion.getUniqueID().toString());
                minionIDs.appendTag(minionID);
            }
            nbtTagCompound.setTag("MinionIDs", minionIDs);
        }

        return nbtTagCompound;
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
