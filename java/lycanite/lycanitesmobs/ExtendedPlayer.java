package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.Beastiary;
import lycanite.lycanitesmobs.api.info.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsPlayer";
	public static Map<EntityPlayer, ExtendedPlayer> extendedPlayers = new HashMap<EntityPlayer, ExtendedPlayer>();
	public static Map<String, NBTTagCompound> backupNBTTags = new HashMap<String, NBTTagCompound>();
	
	// Player Info and Containers:
	public EntityPlayer player;
	public Beastiary beastiary;
	
	// Summoning:
	public int summonFocusCharge = 600;
	public int summonFocusMax = (this.summonFocusCharge * 10);
	public int summonFocus = this.summonFocusMax;
	public Map<Integer, SummonSet> summonSets = new HashMap<Integer, SummonSet>();
	public int selectedSummonSet = 1;
	public int summonSetMax = 5;
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedPlayer(EntityPlayer player) {
		if(backupNBTTags.containsKey(player)) {
			this.loadNBTData(ExtendedPlayer.backupNBTTags.get(player.username));
			backupNBTTags.remove(player);
		}
		
		this.player = player;
		this.beastiary = new Beastiary(player);
		
		extendedPlayers.put(player, this);
	}
	
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		
	}
	
	
	// ==================================================
    //                      Summoning
    // ==================================================
	public SummonSet getSummonSet(int setID) {
		if(setID <= 0) {
			LycanitesMobs.printWarning("", "Attempted to access set " + setID + " but the minimum ID is 1. Player: " + this.player);
			return null;
		}
		else if(setID > this.summonSetMax) {
			LycanitesMobs.printWarning("", "Attempted to access set " + setID + " but the maximum set ID is " + this.summonSetMax + ". Player: " + this.player);
			return null;
		}
		if(!this.summonSets.containsKey(setID))
			this.summonSets.put(setID, new SummonSet(this));
		return this.summonSets.get(setID);
	}
	
	public SummonSet getSelectedSummonSet() {
		if(this.selectedSummonSet != this.validateSummonSetID(this.selectedSummonSet))
			this.setSelectedSummonSet(this.selectedSummonSet); // This is a fail safe and shouldn't really happen, it will fix the current set ID if it is invalid, resending packets too.
		return this.getSummonSet(this.selectedSummonSet);
	}
	
	public void setSelectedSummonSet(int targetSetID) {
		targetSetID = validateSummonSetID(targetSetID);
		this.selectedSummonSet = targetSetID;
	}
	
	/** Use to make sure that the target summoning set ID is valid, it will return it if it is or the best next set ID if it isn't. **/
	public int validateSummonSetID(int targetSetID) {
		targetSetID = Math.max(Math.min(targetSetID, this.summonSetMax), 1);
		while(!this.getSummonSet(targetSetID).isUseable() && targetSetID > 1)
			targetSetID--;
		return targetSetID;
	}
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	public void sendAllSummonSetsToPlayer() {
		if(this.player.worldObj.isRemote)
			return;
		for(byte setID = 1; setID <= this.summonSetMax; setID++) {
			Packet packet = PacketHandler.createPacket(
	        		PacketHandler.PacketType.PLAYER,
	        		PacketHandler.PlayerType.MINION.id,
	        		Byte.valueOf(setID), this.getSummonSet(setID).summonType, this.getSummonSet(setID).getBehaviourBytes()
	        	);
			PacketHandler.sendPacketToPlayer(packet, this.player);
		}
	}
	
	public void sendSummonSetToServer(byte setID) {
		if(!this.player.worldObj.isRemote)
			return;
		SummonSet summonSet = this.getSummonSet(setID);
		Packet packet = PacketHandler.createPacket(
        		PacketHandler.PacketType.PLAYER,
        		PacketHandler.PlayerType.MINION.id,
        		setID, summonSet.summonType, summonSet.getBehaviourBytes()
        	);
        PacketHandler.sendPacketToServer(packet);
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
	@Override
    public void loadNBTData(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag(EXT_PROP_NAME);
		
    	this.beastiary.readFromNBT(extTagCompound);
		
		if(extTagCompound.hasKey("SummonFocus"))
			this.summonFocus = extTagCompound.getInteger("SummonFocus");

		if(extTagCompound.hasKey("SelectedSummonSet"))
			this.selectedSummonSet = extTagCompound.getInteger("SelectedSummonSet");
		
		if(extTagCompound.hasKey("SummonSets")) {
			NBTTagList nbtSummonSets = extTagCompound.getTagList("SummonSets");
			for(int setID = 0; setID < this.summonSetMax; setID++) {
				NBTTagCompound nbtSummonSet = (NBTTagCompound)nbtSummonSets.tagAt(setID);
				SummonSet summonSet = new SummonSet(this);
				summonSet.readFromNBT(nbtSummonSet);
				this.summonSets.put(setID + 1, summonSet);
			}
		}
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
	@Override
    public void saveNBTData(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = new NBTTagCompound();
		
    	this.beastiary.writeToNBT(extTagCompound);
    	
		extTagCompound.setInteger("SummonFocus", this.summonFocus);
		
		extTagCompound.setInteger("SelectedSummonSet", this.selectedSummonSet);
		
		NBTTagList nbtSummonSets = new NBTTagList();
		for(int setID = 0; setID < this.summonSetMax; setID++) {
			NBTTagCompound nbtSummonSet = new NBTTagCompound();
			SummonSet summonSet = this.getSummonSet(setID + 1);
			summonSet.writeToNBT(nbtSummonSet);
			nbtSummonSets.appendTag(nbtSummonSet);
		}
		extTagCompound.setTag("SummonSets", nbtSummonSets);
    	
    	nbtTagCompound.setCompoundTag(EXT_PROP_NAME, extTagCompound);
    }
}
