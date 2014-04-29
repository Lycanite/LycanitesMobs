package lycanite.lycanitesmobs.api.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
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
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	/** Sends a new Beastiary entry (CreatureKnowledge) to the client. Shouldn't really be needed, just add it client side. **/
	public void sendNewToClient(CreatureKnowledge newKnowledge) {
		Packet packet = PacketHandler.createPacket(
        		PacketHandler.PacketType.PLAYER,
        		PacketHandler.PlayerType.BEASTIARY.id,
        		newKnowledge.creatureName,
        		newKnowledge.completion
        	);
		PacketHandler.sendPacketToPlayer(packet, this.player);
	}
	
	/** Sends the whole Beastiary progress to the client, use sparingly! **/
	public void sendAllToClient() {
		Object[] creatureKnowledgeData = new Object[2 + (this.creatureKnowledgeList.size() * 2)];
		int index = 0;
		creatureKnowledgeData[index++] = PacketHandler.PlayerType.BEASTIARY_ALL.id;
		creatureKnowledgeData[index++] = this.creatureKnowledgeList.size();
		for(CreatureKnowledge creatureKnowledge : this.creatureKnowledgeList.values()) {
			creatureKnowledgeData[index++] = creatureKnowledge.creatureName;
			creatureKnowledgeData[index++] = creatureKnowledge.completion;
		}
		Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, creatureKnowledgeData);
		PacketHandler.sendPacketToPlayer(packet, this.player);
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
