package lycanite.lycanitesmobs.api.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.network.MessageBeastiary;
import lycanite.lycanitesmobs.api.network.MessageCreatureKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Beastiary {
	public EntityPlayer player;
	public Map<String, CreatureKnowledge> creatureKnowledgeList = new HashMap<String, CreatureKnowledge>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public Beastiary(EntityPlayer player) {
		this.player = player;
	}
	
	
    // ==================================================
    //                     Knowledge
    // ==================================================
	public void newKnowledgeList(Map<String, CreatureKnowledge> newKnowledgeList) {
		this.creatureKnowledgeList = newKnowledgeList;
	}

	public void addToKnowledgeList(CreatureKnowledge newKnowledge) {
		if(ObjectManager.getMob(newKnowledge.creatureName) == null)
			return;
		this.creatureKnowledgeList.put(newKnowledge.creatureName, newKnowledge);
	}
	
	public boolean hasFullKnowledge(String creatureName) {
		if(!this.creatureKnowledgeList.containsKey(creatureName))
			return false;
		if(this.creatureKnowledgeList.get(creatureName).completion < 1)
			return false;
		return true;
	}
	
	/**
	 * Used to determine if any creatures from the specific group are in the players beastiary.
	 * @param group Group to check with.
	 * @return True if the player has at least one creature form the specific group.
	 */
	public boolean hasCreatureFromGroup(GroupInfo group) {
		if(this.creatureKnowledgeList.size() == 0)
			return false;
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : this.creatureKnowledgeList.entrySet()) {
			if(creatureKnowledgeEntry.getValue() != null)
				if(creatureKnowledgeEntry.getValue().creatureInfo.group == group)
					return true;
		}
		return false;
	}
	
	
    // ==================================================
    //                     Summoning
    // ==================================================
	public Map<Integer, String> getSummonableList() {
		Map<Integer, String> minionList = new HashMap<Integer, String>();
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
	/** Sends a new Beastiary entry (CreatureKnowledge) to the client. Shouldn't really be needed, just add it client side. **/
	public void sendNewToClient(CreatureKnowledge newKnowledge) {
		MessageCreatureKnowledge message = new MessageCreatureKnowledge(newKnowledge);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
	}
	
	/** Sends the whole Beastiary progress to the client, use sparingly! **/
	public void sendAllToClient() {
		MessageBeastiary message = new MessageBeastiary(this);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	if(!nbtTagCompound.hasKey("CreatureKnowledge"))
    		return;
    	this.newKnowledgeList(new HashMap<String, CreatureKnowledge>());
    	NBTTagList knowledgeList = nbtTagCompound.getTagList("CreatureKnowledge", 10);
    	for(int i = 0; i < knowledgeList.tagCount(); ++i) {
	    	NBTTagCompound nbtKnowledge = (NBTTagCompound)knowledgeList.getCompoundTagAt(i);
    		if(nbtKnowledge.hasKey("CreatureName") && nbtKnowledge.hasKey("Completion")) {
	    		CreatureKnowledge creatureKnowledge = new CreatureKnowledge(
	    				player,
	    				nbtKnowledge.getString("CreatureName"),
	    				nbtKnowledge.getDouble("Completion")
	    			);
	    		this.addToKnowledgeList(creatureKnowledge);
    		}
    	}
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	NBTTagList knowledgeList = new NBTTagList();
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : creatureKnowledgeList.entrySet()) {
			CreatureKnowledge creatureKnowledge = creatureKnowledgeEntry.getValue();
			NBTTagCompound nbtKnowledge = new NBTTagCompound();
			nbtKnowledge.setString("CreatureName", creatureKnowledge.creatureName);
			nbtKnowledge.setDouble("Completion", creatureKnowledge.completion);
			knowledgeList.appendTag(nbtKnowledge);
		}
		nbtTagCompound.setTag("CreatureKnowledge", knowledgeList);
    }
}
