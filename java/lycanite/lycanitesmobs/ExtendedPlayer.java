package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.Beastiary;
import lycanite.lycanitesmobs.api.info.SummonSet;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsPlayer";
	public static Map<String, NBTTagCompound> backupNBTTags = new HashMap<String, NBTTagCompound>();
	
	// Player Info and Containers:
	public EntityPlayer player;
	public Beastiary beastiary;
	
	public long currentTick = 0;
	public boolean needsFirstSync = true;
	
	// Summoning:
	public int summonFocusCharge = 600;
	public int summonFocusMax = (this.summonFocusCharge * 10);
	public int summonFocus = this.summonFocusMax;
	public Map<Integer, SummonSet> summonSets = new HashMap<Integer, SummonSet>();
	public int selectedSummonSet = 1;
	public int summonSetMax = 5;
	
	// ==================================================
    //                   Get for Player
    // ==================================================
	public static ExtendedPlayer getForPlayer(EntityPlayer player) {
		if(player == null) {
			LycanitesMobs.printWarning("", "Tried to access an ExtendedPlayer from a null EntityPlayer.");
			return null;
		}
		IExtendedEntityProperties playerIExt = player.getExtendedProperties(EXT_PROP_NAME);
		ExtendedPlayer playerExt;
		if(playerIExt != null)
			playerExt = (ExtendedPlayer)playerIExt;
		else
			playerExt = new ExtendedPlayer(player);
		
		return playerExt;
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.beastiary = new Beastiary(player);
		
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, this);
	}
	
	
	// ==================================================
    //                    Join World
    // ==================================================
	public void onJoinWorld() {
		if(this.player.worldObj.isRemote)
			return;
		// Check for Backup:
		if(backupNBTTags.containsKey(this.player.getCommandSenderName())) {
			this.loadNBTData(backupNBTTags.get(this.player.getCommandSenderName()));
			backupNBTTags.remove(this.player.getCommandSenderName());
		}
	}
	
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		
	}
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the EventListener, runs any logic on the main player entity's main update loop. **/
	public void onUpdate() {
		boolean creative = this.player.capabilities.isCreativeMode;
		
		// Summoning Focus Stat Update:
		if(!player.worldObj.isRemote) {
			this.summonFocus = Math.min(Math.max(this.summonFocus, 0), this.summonFocusMax);
			if(this.summonFocus < this.summonFocusMax) {
				this.summonFocus++;
			}
			if(!creative && this.currentTick % 20 == 0) {
				if(this.summonFocus < this.summonFocusMax || (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemStaffSummoning)) {
					Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.SUMMONFOCUS.id, this.summonFocus);
					PacketHandler.sendPacketToPlayer(packet, this.player);
				}
			}
		}
		
		// Initial Network Sync:
		if(!this.player.worldObj.isRemote && this.needsFirstSync) {
			this.beastiary.sendAllToClient();
			this.sendAllSummonSetsToPlayer();
			Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.MINION_SELECT.id, (byte)this.selectedSummonSet);
			PacketHandler.sendPacketToPlayer(packet, this.player);
		}
		needsFirstSync = false;
		
		this.currentTick++;
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
    //                      Beastiary
    // ==================================================
	/** Returns the player's beastiary, will also update the client, access the beastiary variable directly when loading NBT data as the network player is null at first. **/
	public Beastiary getBeastiary() {
		return this.beastiary;
	}
	
	
	// ==================================================
    //                    Death Backup
    // ==================================================
	public void onDeath() {
		if(backupNBTTags.containsKey(this.player.getCommandSenderName()))
			return;
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.saveNBTData(nbtTagCompound);
		backupNBTTags.put(this.player.getCommandSenderName(), nbtTagCompound);
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
			NBTTagList nbtSummonSets = extTagCompound.getTagList("SummonSets", 10);
			for(int setID = 0; setID < this.summonSetMax; setID++) {
				NBTTagCompound nbtSummonSet = (NBTTagCompound)nbtSummonSets.getCompoundTagAt(setID);
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
    	
    	nbtTagCompound.setTag(EXT_PROP_NAME, extTagCompound);
    }
}
