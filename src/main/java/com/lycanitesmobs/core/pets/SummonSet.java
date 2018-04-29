package com.lycanitesmobs.core.pets;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.nbt.NBTTagCompound;

public class SummonSet {
	public ExtendedPlayer playerExt;
	
	// Summoning Details:
	public String summonType = "";
	public int subspecies = 0;
    public boolean summonableOnly = true;
	public boolean sitting = false;
	public boolean following = true;
	public boolean passive = false;
	public boolean assist = true;
	public boolean aggressive = false;
	public boolean pvp = false;
	
	// Behaviour Bits:
	/** A list bytes for each behaviour, used when syncing network packets. **/
	public enum BEHAVIOUR_ID {
		SITTING((byte)1), FOLLOWING((byte)2), PASSIVE((byte)4), ASSIST((byte)8), AGGRESSIVE((byte)16), PVP((byte)32);
		public byte id;
		BEHAVIOUR_ID(byte i) { id = i; }
	}
	
	// ==================================================
    //                   Static Methods
    // ==================================================
	public static boolean isSummonableCreature(String creatureName) {
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(creatureName);
		if(creatureInfo == null)
			return false;
		if(creatureInfo.dummy)
			return false;
		return creatureInfo.isSummonable();
	}
	
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public SummonSet(ExtendedPlayer playerExt) {
		this.playerExt = playerExt;
	}
	
	
	// ==================================================
    //                      Behaviour
    // ==================================================
	public void setSummonType(String summonType) {
		/*if(this.playerExt != null && this.summonableOnly && (!this.playerExt.beastiary.hasFullKnowledge(this.summonType) || !isSummonableCreature(this.summonType)))
			this.summonType = "";
        else*/
		if(summonType != null) {
			this.summonType = summonType.toLowerCase();
		}
		else {
			this.summonType = null;
		}
	}

	public void setSubspecies(int subspecies) {
		this.subspecies = subspecies;
	}

	public int getSubspecies() {
		return this.subspecies;
	}
	
	public boolean getSitting() {
		return this.sitting;
	}

	public boolean getFollowing() {
		return this.following;
	}

	public boolean getPassive() {
		return this.passive;
	}

	public boolean getAssist() {
		return this.assist;
	}

	public boolean getAggressive() {
		return this.aggressive;
	}

	public boolean getPVP() {
		return this.pvp;
	}

	/**
	 * Gets the creature info used by this summon set.
	 * @return The summon set creature info.
	 */
	public CreatureInfo getCreatureInfo() {
		return CreatureManager.getInstance().getCreature(this.summonType.toLowerCase());
	}

    /**
	 * Applies all behaviour in this set to the provided entity.
	 */
    public void applyBehaviour(EntityCreatureTameable minion) {
        minion.setSitting(this.getSitting());
        minion.setFollowing(this.getFollowing());
        minion.setPassive(this.getPassive());
		minion.setAssist(this.getAssist());
        minion.setAggressive(this.getAggressive());
        minion.setPVP(this.getPVP());
    }

    /**
	 * Copies the provided entity's behaviour into this summon set's behaviour.
	 */
    public void updateBehaviour(EntityCreatureTameable minion) {
        this.sitting = minion.isSitting();
        this.following = minion.isFollowing();
        this.passive = minion.isPassive();
        this.assist = minion.isAssisting();
		this.aggressive = minion.isAggressive();
        this.pvp = minion.isPVP();
    }
	
	
	// ==================================================
    //                        Info
    // ==================================================
	/** Returns true if this summon set has a valid mob to summon and can be used by staves, etc. **/
	public boolean isUseable() {
		if(this.summonType == null || "".equals(this.summonType) || CreatureManager.getInstance().getCreature(this.summonType) == null || !isSummonableCreature(this.summonType))
			return false;
		return true;
	}
	
	/** Returns the class of the creature to summon. **/
	public Class getCreatureClass() {
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(this.summonType);
		if(creatureInfo == null) {
			return null;
		}
		return creatureInfo.entityClass;
	}
	
	
	// ==================================================
    //                        Sync
    // ==================================================
	public void readFromPacket(String summonType, int subspecies, byte behaviour) {
		this.setSummonType(summonType);
		this.setSubspecies(subspecies);
		this.setBehaviourByte(behaviour);
	}

    public void setBehaviourByte(byte behaviour) {
        this.sitting = (behaviour & BEHAVIOUR_ID.SITTING.id) > 0;
        this.following = (behaviour & BEHAVIOUR_ID.FOLLOWING.id) > 0;
        this.passive = (behaviour & BEHAVIOUR_ID.PASSIVE.id) > 0;
		this.assist = (behaviour & BEHAVIOUR_ID.ASSIST.id) > 0;
        this.aggressive = (behaviour & BEHAVIOUR_ID.AGGRESSIVE.id) > 0;
        this.pvp = (behaviour & BEHAVIOUR_ID.PVP.id) > 0;
    }

	public byte getBehaviourByte() {
		byte behaviour = 0;
		if(this.getSitting()) behaviour += BEHAVIOUR_ID.SITTING.id;
		if(this.getFollowing()) behaviour += BEHAVIOUR_ID.FOLLOWING.id;
		if(this.getPassive()) behaviour += BEHAVIOUR_ID.PASSIVE.id;
		if(this.getAggressive()) behaviour += BEHAVIOUR_ID.AGGRESSIVE.id;
		if(this.getPVP()) behaviour += BEHAVIOUR_ID.PVP.id;
		return behaviour;
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("SummonType"))
    		this.setSummonType(nbtTagCompound.getString("SummonType"));

		if(nbtTagCompound.hasKey("Subspecies"))
			this.setSubspecies(nbtTagCompound.getInteger("Subspecies"));
    	
    	if(nbtTagCompound.hasKey("Sitting"))
    		this.sitting = nbtTagCompound.getBoolean("Sitting");
    	
    	if(nbtTagCompound.hasKey("Following"))
    		this.following = nbtTagCompound.getBoolean("Following");
    	
    	if(nbtTagCompound.hasKey("Passive"))
    		this.passive = nbtTagCompound.getBoolean("Passive");
    	
    	if(nbtTagCompound.hasKey("Aggressive"))
    		this.aggressive = nbtTagCompound.getBoolean("Aggressive");
    	
    	if(nbtTagCompound.hasKey("PVP"))
    		this.pvp = nbtTagCompound.getBoolean("PVP");
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setString("SummonType", this.summonType);

		nbtTagCompound.setInteger("Subspecies", this.getSubspecies());
    	
    	nbtTagCompound.setBoolean("Sitting", this.sitting);
    	
    	nbtTagCompound.setBoolean("Following", this.following);
    	
    	nbtTagCompound.setBoolean("Passive", this.passive);
    	
    	nbtTagCompound.setBoolean("Aggressive", this.aggressive);
    	
    	nbtTagCompound.setBoolean("PVP", this.pvp);
    }
}
