package com.lycanitesmobs.core.info;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.network.MessageBeastiary;
import com.lycanitesmobs.core.network.MessageCreatureKnowledge;
import com.lycanitesmobs.core.pets.SummonSet;
import jline.internal.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Beastiary {
	public ExtendedPlayer extendedPlayer;
	public Map<String, CreatureKnowledge> creatureKnowledgeList = new HashMap<>();

	/**
	 * Constructor
	 * @param extendedPlayer The Extended Player this Beastiary belongs to.
	 */
	public Beastiary(ExtendedPlayer extendedPlayer) {
		this.extendedPlayer = extendedPlayer;
	}
	
	
    // ==================================================
    //                     Knowledge
    // ==================================================
	/**
	 * Adds Creature Knowledge to this Beastiary after checking rank, etc.
	 * @param newKnowledge The new knowledge to add.
	 * @return The increase of knowledge rank or 0 on failure.
	 */
	public int addCreatureKnowledge(CreatureKnowledge newKnowledge) {
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(newKnowledge.creatureName);
		if(creatureInfo == null)
			return 0;
		if(creatureInfo.dummy)
			return 0;

		CreatureKnowledge currentKnowledge = this.getCreatureKnowledge(creatureInfo.getName());
		if(currentKnowledge != null) {
			if(currentKnowledge.rank >= newKnowledge.rank) {
				return 0;
			}
			int rankIncrease = newKnowledge.rank - currentKnowledge.rank;
			currentKnowledge.rank = newKnowledge.rank;
			return rankIncrease;
		}

		this.creatureKnowledgeList.put(newKnowledge.creatureName, newKnowledge);
		return newKnowledge.rank;
	}


	/**
	 * Attemtp to add Creature Knowledge to this Beastiary based on the provided entity and sends feedback to the player.
	 * @param entity The entity being descovered.
	 * @param rank The Knowledge rank being descovered.
	 * @param knownMessage If true, if the creature is already known at the same or higher rank a message will be sent to the player.
	 * @return True if new knowledge is gained and false if not.
	 */
	public boolean discoverCreature(Entity entity, int rank, boolean knownMessage) {
		// Invalid Entity:
		if(!(entity instanceof EntityCreatureBase)) {
			if (!this.extendedPlayer.player.getEntityWorld().isRemote) {
				this.extendedPlayer.player.sendMessage(new TextComponentString(I18n.translateToLocal("message.beastiary.unknown")));
			}
			return false;
		}
		if(entity instanceof EntityFear) {
			return false;
		}

		CreatureInfo creatureInfo = ((EntityCreatureBase)entity).creatureInfo;
		CreatureKnowledge newKnowledge = new CreatureKnowledge(this.extendedPlayer.getBeastiary(), creatureInfo.getName(), rank);
		int rankChange = this.extendedPlayer.getBeastiary().addCreatureKnowledge(newKnowledge);

		// Already Known:
		if(rankChange <= 0) {
			if(knownMessage) {
				this.sendKnownMessage(newKnowledge);
			}
			return false;
		}

		// Success:
		this.extendedPlayer.player.addStat(ObjectManager.getStat(creatureInfo.name + ".learn"), 1);
		this.sendAddedMessage(newKnowledge);
		this.sendToClient(newKnowledge);
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			for(int i = 0; i < 32; ++i) {
				entity.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
						entity.posX + (4.0F * this.extendedPlayer.player.getRNG().nextFloat()) - 2.0F,
						entity.posY + (4.0F * this.extendedPlayer.player.getRNG().nextFloat()) - 2.0F,
						entity.posZ + (4.0F * this.extendedPlayer.player.getRNG().nextFloat()) - 2.0F,
						0.0D, 0.0D, 0.0D);
			}
		}
		return true;
	}


	/**
	 * Sends a message to the player on gaining Creature Knowledge.
	 * @param creatureKnowledge The creature knowledge that was added.
	 */
	public void sendAddedMessage(CreatureKnowledge creatureKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			return;
		}
		CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
		String message = I18n.translateToLocal("message.beastiary.new");
		message = message.replace("%creature%", creatureInfo.getTitle());
		message = message.replace("%rank%", "" + creatureKnowledge.rank);
		this.extendedPlayer.player.sendMessage(new TextComponentString(message));
		if(creatureInfo.isSummonable()) {
			String summonMessage = I18n.translateToLocal("message.beastiary.summonable");
			if(creatureKnowledge.rank >= 3) {
				summonMessage = I18n.translateToLocal("message.beastiary.summonable.skins");
			}
			else if(creatureKnowledge.rank == 2) {
				summonMessage = I18n.translateToLocal("message.beastiary.summonable.colors");
			}
			summonMessage = summonMessage.replace("%creature%", creatureInfo.getTitle());
			this.extendedPlayer.player.sendMessage(new TextComponentString(summonMessage));
		}
	}


	/**
	 * Sends a message to the player if they attempt to add a creature that they already know.
	 * @param creatureKnowledge The creature knowledge that was trying to be added.
	 */
	public void sendKnownMessage(CreatureKnowledge creatureKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			return;
		}
		CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
		CreatureKnowledge currentKnowledge = this.extendedPlayer.getBeastiary().getCreatureKnowledge(creatureInfo.getName());
		String message = I18n.translateToLocal("message.beastiary.known");
		message = message.replace("%creature%", creatureInfo.getTitle());
		message = message.replace("%rank%", "" + currentKnowledge.rank);
		this.extendedPlayer.player.sendMessage(new TextComponentString(message));
	}


	/**
	 * Returns the current knowledge of the provided creature. Use CreatureKnowledge.rank to get the current rank of knowledge the player has.
	 * @param creatureName The name of the creature to get the knowledge of.
	 * @return The creature knowledge or knowledge if there is no knowledge.
	 */
	@Nullable
	public CreatureKnowledge getCreatureKnowledge(String creatureName) {
		if(!this.creatureKnowledgeList.containsKey(creatureName)) {
			return null;
		}
		return this.creatureKnowledgeList.get(creatureName);
	}


	/**
	 * Returns if this Beastiary has the provided knowledge rank or higher.
	 * @param creatureName The name of the creature to check the knowledge rank of.
	 * @param rank The minimum knowledge rank required.
	 * @return True if the knowledge rank is met or exceeded.
	 */
	public boolean hasKnowledgeRank(String creatureName, int rank) {
		CreatureKnowledge creatureKnowledge = this.getCreatureKnowledge(creatureName);
		if(creatureKnowledge == null) {
			return false;
		}
		return creatureKnowledge.rank >= rank;
	}

	
	/**
	 * Returns how many creatures of the specified group the player has descovered.
	 * @param group Group to check with.
	 * @return True if the player has at least one creature form the specific group.
	 */
	public int getCreaturesDescovered(GroupInfo group) {
		if(this.creatureKnowledgeList.size() == 0) {
			return 0;
		}

		int creaturesDescovered = 0;
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : this.creatureKnowledgeList.entrySet()) {
			if(creatureKnowledgeEntry.getValue() != null) {
				if (creatureKnowledgeEntry.getValue().getCreatureInfo().group == group) {
					creaturesDescovered++;
				}
			}
		}
		return creaturesDescovered;
	}
	
	
    // ==================================================
    //                     Summoning
    // ==================================================
	public Map<Integer, String> getSummonableList() {
		Map<Integer, String> minionList = new HashMap<>();
		int minionIndex = 0;
		for(String minionName : this.creatureKnowledgeList.keySet()) {
			if(SummonSet.isSummonableCreature(minionName)) {
				minionList.put(minionIndex++, minionName);
			}
		}
		return minionList;
	}
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	/** Sends CreatureKnowledge to the client. For when it's added or changed server side but needs updated client side. **/
	public void sendToClient(CreatureKnowledge newKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			return;
		}
		MessageCreatureKnowledge message = new MessageCreatureKnowledge(newKnowledge);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
	}
	
	/** Sends the whole Beastiary progress to the client, use sparingly! **/
	public void sendAllToClient() {
		MessageBeastiary message = new MessageBeastiary(this);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	if(!nbtTagCompound.hasKey("CreatureKnowledge"))
    		return;
    	this.creatureKnowledgeList.clear();
    	NBTTagList knowledgeList = nbtTagCompound.getTagList("CreatureKnowledge", 10);
    	for(int i = 0; i < knowledgeList.tagCount(); ++i) {
	    	NBTTagCompound nbtKnowledge = knowledgeList.getCompoundTagAt(i);
    		if(nbtKnowledge.hasKey("CreatureName")) {
    			String creatureName = nbtKnowledge.getString("CreatureName");
				int rank = 0;
				if(nbtKnowledge.hasKey("Rank")) {
					rank = nbtKnowledge.getInteger("Rank");
				}
				else if(nbtKnowledge.hasKey("Completion")) {
					rank = 2;
				}
	    		CreatureKnowledge creatureKnowledge = new CreatureKnowledge(
                        this,
	    				creatureName,
	    				rank
	    			);
	    		this.addCreatureKnowledge(creatureKnowledge);
    		}
    	}
    }

    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	NBTTagList knowledgeList = new NBTTagList();
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : creatureKnowledgeList.entrySet()) {
			CreatureKnowledge creatureKnowledge = creatureKnowledgeEntry.getValue();
			NBTTagCompound nbtKnowledge = new NBTTagCompound();
			nbtKnowledge.setString("CreatureName", creatureKnowledge.creatureName);
			nbtKnowledge.setInteger("Rank", creatureKnowledge.rank);
			knowledgeList.appendTag(nbtKnowledge);
		}
		nbtTagCompound.setTag("CreatureKnowledge", knowledgeList);
    }
}
