package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.core.capabilities.IExtendedPlayer;
import lycanite.lycanitesmobs.core.info.Beastiary;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.info.MobInfo;
import lycanite.lycanitesmobs.core.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.core.network.*;
import lycanite.lycanitesmobs.core.pets.DonationFamiliars;
import lycanite.lycanitesmobs.core.pets.PetEntry;
import lycanite.lycanitesmobs.core.pets.PetManager;
import lycanite.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;

import java.util.HashMap;
import java.util.Map;

public class ExtendedPlayer implements IExtendedPlayer {
    public static Map<EntityPlayer, ExtendedPlayer> clientExtendedPlayers = new HashMap<EntityPlayer, ExtendedPlayer>();
	public static Map<String, NBTTagCompound> backupNBTTags = new HashMap<String, NBTTagCompound>();
	
	// Player Info and Containers:
	public EntityPlayer player;
	public Beastiary beastiary;
	public GroupInfo beastiaryGroup;
	public MobInfo beastiaryCreature;
	public String beastiaryCategory;
	public PetManager petManager;
	
	public long currentTick = 0;
	public boolean needsFirstSync = true;
	
	// Action Controls:
	public byte controlStates = 0;
	public static enum CONTROL_ID {
		JUMP((byte)1), MOUNT_ABILITY((byte)2), MOUNT_INVENTORY((byte)4), ATTACK((byte)8);
		public byte id;
		private CONTROL_ID(byte i) { id = i; }
	}
    public boolean hasAttacked = false; // If true, this entity has attacked this tick.

	// Spirit:
	public int spiritCharge = 100;
	public int spiritMax = (this.spiritCharge * 10);
	public int spirit = this.spiritMax;
	public int spiritReserved = 0;
	
	// Summoning:
	public int summonFocusCharge = 600;
	public int summonFocusMax = (this.summonFocusCharge * 10);
	public int summonFocus = this.summonFocusMax;
	public Map<Integer, SummonSet> summonSets = new HashMap<Integer, SummonSet>();
	public int selectedSummonSet = 1;
	public int summonSetMax = 5;

    // Familiars:
    private boolean petManagerSetup = false;
	
	// ==================================================
    //                   Get for Player
    // ==================================================
	public static ExtendedPlayer getForPlayer(EntityPlayer player) {
		if(player == null) {
			//LycanitesMobs.printWarning("", "Tried to access an ExtendedPlayer from a null EntityPlayer.");
			return null;
		}

        // Client Side:
        if(player.worldObj != null && player.worldObj.isRemote) {
            if(clientExtendedPlayers.containsKey(player)) {
                ExtendedPlayer extendedPlayer = clientExtendedPlayers.get(player);
                extendedPlayer.setPlayer(player);
                return extendedPlayer;
            }
            ExtendedPlayer extendedPlayer = new ExtendedPlayer();
            extendedPlayer.setPlayer(player);
            clientExtendedPlayers.put(player, extendedPlayer);
        }

        // Server Side:
        IExtendedPlayer iExtendedPlayer = player.getCapability(LycanitesMobs.EXTENDED_PLAYER, null);
        if(!(iExtendedPlayer instanceof ExtendedPlayer))
            return null;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)iExtendedPlayer;
        if(extendedPlayer.getPlayer() != player)
            extendedPlayer.setPlayer(player);
        return extendedPlayer;
	}


    // ==================================================
    //                    Constructor
    // ==================================================
    public ExtendedPlayer() {
        this.beastiary = new Beastiary(this);
        this.petManager = new PetManager(this.player);
    }
	
	
	// ==================================================
    //                    Player Entity
    // ==================================================
    /** Called when the player entity is being cloned, backups all data so that it can be loaded into a new ExtendedPlayer for the clone. **/
    public void backupPlayer() {
        if(this.player != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            this.writeNBT(nbtTagCompound);
            backupNBTTags.put(this.player.getName(), nbtTagCompound);
        }
    }

    /** Initially sets the player entity and loads any backup data, if the entity is being cloned from another call backupPlayer() instead so that the clone's ExtendedPlayer can load it. **/
	public void setPlayer(EntityPlayer player) {
        this.player = player;
        this.petManager.host = player;
        if(this.player.worldObj == null || this.player.worldObj.isRemote)
            return;
        if(backupNBTTags.containsKey(this.player.getName())) {
            this.readNBT(backupNBTTags.get(this.player.getName()));
            backupNBTTags.remove(this.player.getName());
        }
	}

    public EntityPlayer getPlayer() {
        return this.player;
    }

    /** Returns true if the provided entity is within melee attack range and is considered large. This is used for when the vanilla attack range fails on big entities. **/
    public boolean canMeleeBigEntity(Entity targetEntity) {
        if(targetEntity == null || !(targetEntity instanceof EntityLivingBase))
            return false;
        if(targetEntity.height <= 4 && targetEntity.width <= 4)
            return false;
        double heightOffset = this.player.posY - targetEntity.posY;
        double heightCompensation = 0;
        if(heightOffset > 0)
            heightCompensation = Math.min(heightOffset, targetEntity.height);
        double distance = Math.sqrt(this.player.getDistanceSqToEntity(targetEntity));
        double range = 6 + heightCompensation + (targetEntity.width / 2);
        return distance <= range;
    }

    /** Makes this player attempt to melee attack. This is typically used for when the vanilla attack range fails on big entities. **/
    public void meleeAttack(Entity targetEntity) {
        if(!this.hasAttacked && this.player.getHeldItemMainhand() != null && this.canMeleeBigEntity(targetEntity)) {
            this.player.attackTargetEntityWithCurrentItem(targetEntity);
            this.player.resetCooldown();
            this.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the EventListener, runs any logic on the main player entity's main update loop. **/
	public void onUpdate() {
        this.hasAttacked = false;
		boolean creative = this.player.capabilities.isCreativeMode;

		// Stats:
		boolean sync = false;

		// Spirit Stat Update:
		this.spirit = Math.min(Math.max(this.spirit, 0), this.spiritMax - this.spiritReserved);
		if(this.spirit < this.spiritMax - this.spiritReserved) {
			this.spirit++;
			if(!this.player.worldObj.isRemote && this.currentTick % 20 == 0 || this.spirit == this.spiritMax - this.spiritReserved) {
				sync = true;
			}
		}

		// Summoning Focus Stat Update:
		this.summonFocus = Math.min(Math.max(this.summonFocus, 0), this.summonFocusMax);
		if(this.summonFocus < this.summonFocusMax) {
			this.summonFocus++;
			if(!this.player.worldObj.isRemote && !creative && this.currentTick % 20 == 0
					|| this.summonFocus < this.summonFocusMax
					|| (this.player.getHeldItemMainhand() != null && this.player.getHeldItemMainhand().getItem() instanceof ItemStaffSummoning)
                    || (this.player.getHeldItemOffhand() != null && this.player.getHeldItemOffhand().getItem() instanceof ItemStaffSummoning)) {
				sync = true;
			}
		}

		// Sync Stats To Client:
		if(!this.player.worldObj.isRemote) {
			if(sync) {
				MessagePlayerStats message = new MessagePlayerStats(this);
				LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP) this.player);
			}
		}

		// Pet Manager Setup:
		if(!this.player.worldObj.isRemote && !this.petManagerSetup) {

			// Load Familiars:
			Map<String, PetEntry> playerFamiliars = DonationFamiliars.instance.getFamiliarsForPlayer(this.player);
			if(playerFamiliars != null) {
				this.petManager.clearEntries("familiar");
				for(PetEntry petEntry : playerFamiliars.values()) {
					if(!this.petManager.hasEntry(petEntry)) {
						this.petManager.addEntry(petEntry);
					}
				}
				this.sendPetEntriesToPlayer("familiar");
			}

			this.petManagerSetup = true;
		}
		
		// Initial Network Sync:
		if(!this.player.worldObj.isRemote && this.needsFirstSync) {
			this.beastiary.sendAllToClient();
			this.sendAllSummonSetsToPlayer();
			MessageSummonSetSelection message = new MessageSummonSetSelection(this);
			LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
		}

        // Pet Manager:
        this.petManager.onUpdate(this.player.worldObj);
		
		this.currentTick++;
		this.needsFirstSync = false;
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
    //                    Beastiary
    // ==================================================
	/** Returns the player's beastiary, will also update the client, access the beastiary variable directly when loading NBT data as the network player is null at first. **/
	public Beastiary getBeastiary() {
		return this.beastiary;
	}
	
	
	// ==================================================
    //                      Death
    // ==================================================
	public void onDeath() {

	}
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	public void sendPetEntriesToPlayer(String entryType) {
		if(this.player.worldObj.isRemote) return;
		for(PetEntry petEntry : this.petManager.allEntries.values()) {
            if(entryType.equals(petEntry.getType()) || "".equals(entryType)) {
                MessagePetEntry message = new MessagePetEntry(this, petEntry);
                LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
            }
		}
	}

    public void sendPetEntryToPlayer(PetEntry petEntry) {
        if(this.player.worldObj.isRemote) return;
        MessagePetEntry message = new MessagePetEntry(this, petEntry);
        LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
    }

	public void sendPetEntryToServer(PetEntry petEntry) {
		if(!this.player.worldObj.isRemote) return;
        MessagePetEntry message = new MessagePetEntry(this, petEntry);
		LycanitesMobs.packetHandler.sendToServer(message);
	}

	public void sendPetEntryRemoveRequest(PetEntry petEntry) {
		if(!this.player.worldObj.isRemote) return;
		petEntry.remove();
		MessagePetEntryRemove message = new MessagePetEntryRemove(this, petEntry);
		LycanitesMobs.packetHandler.sendToServer(message);
	}

    public void sendAllSummonSetsToPlayer() {
        if(this.player.worldObj.isRemote) return;
        for(byte setID = 1; setID <= this.summonSetMax; setID++) {
            MessageSummonSet message = new MessageSummonSet(this, setID);
            LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
        }
    }

    public void sendSummonSetToServer(byte setID) {
        if(!this.player.worldObj.isRemote) return;
        MessageSummonSet message = new MessageSummonSet(this, setID);
        LycanitesMobs.packetHandler.sendToServer(message);
    }
	
	
	// ==================================================
    //                     Controls
    // ==================================================
	public void updateControlStates(byte controlStates) {
		this.controlStates = controlStates;
	}
	
	public boolean isControlActive(CONTROL_ID controlID) {
		return (this.controlStates & controlID.id) > 0;
	}
	
	
	// ==================================================
    //                 Request GUI Data
    // ==================================================
	public void requestGUI(byte guiID) {
		if(guiID == GuiHandler.PlayerGuiType.PET_MANAGER.id)
			this.sendPetEntriesToPlayer("pet");
		if(guiID == GuiHandler.PlayerGuiType.MOUNT_MANAGER.id)
			this.sendPetEntriesToPlayer("mount");
        if(guiID == GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id)
            this.sendPetEntriesToPlayer("familiar");
	}


	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag("LycanitesMobsPlayer");

    	this.beastiary.readFromNBT(extTagCompound);
        this.petManager.readFromNBT(extTagCompound);

		if(extTagCompound.hasKey("SummonFocus"))
			this.summonFocus = extTagCompound.getInteger("SummonFocus");

		if(extTagCompound.hasKey("Spirit"))
			this.spirit = extTagCompound.getInteger("Spirit");

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
    public void writeNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = new NBTTagCompound();

    	this.beastiary.writeToNBT(extTagCompound);
		this.petManager.writeToNBT(extTagCompound);

		extTagCompound.setInteger("SummonFocus", this.summonFocus);
		extTagCompound.setInteger("Spirit", this.spirit);
		extTagCompound.setInteger("SelectedSummonSet", this.selectedSummonSet);

		NBTTagList nbtSummonSets = new NBTTagList();
		for(int setID = 0; setID < this.summonSetMax; setID++) {
			NBTTagCompound nbtSummonSet = new NBTTagCompound();
			SummonSet summonSet = this.getSummonSet(setID + 1);
			summonSet.writeToNBT(nbtSummonSet);
			nbtSummonSets.appendTag(nbtSummonSet);
		}
		extTagCompound.setTag("SummonSets", nbtSummonSets);

    	nbtTagCompound.setTag("LycanitesMobsPlayer", extTagCompound);
    }
}
