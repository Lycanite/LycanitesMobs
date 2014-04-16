package lycanite.lycanitesmobs.api.info;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Beastiary {
	public EntityPlayer player;
	public List<CreatureKnowledge> creatureKnowledgeList = new ArrayList<CreatureKnowledge>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public Beastiary(EntityPlayer player) {
		this.player = player;
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	if(!nbtTagCompound.hasKey("CreatureKnowledge"))
    		return;
    	NBTTagList knowledgeList = nbtTagCompound.getTagList("CreatureKnowledge");
    	for(int i = 0; i < knowledgeList.tagCount(); ++i) {
	    	NBTTagCompound knowledgeEntry = (NBTTagCompound)knowledgeList.tagAt(i);
    		if(knowledgeEntry.hasKey("CreatureName") && knowledgeEntry.hasKey("Completion")) {
	    		CreatureKnowledge creatureKnowledge = new CreatureKnowledge(
	    				player,
	    				knowledgeEntry.getString("CreatureName"),
	    				knowledgeEntry.getDouble("Completion")
	    			);
	    		creatureKnowledgeList.add(creatureKnowledge);
    		}
    	}
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	NBTTagList knowledgeList = new NBTTagList();
		for(CreatureKnowledge creatureKnowledge : creatureKnowledgeList) {
			NBTTagCompound knowledgeEntry = new NBTTagCompound();
			knowledgeEntry.setString("CreatureName", creatureKnowledge.creatureName);
			knowledgeEntry.setDouble("Completion", creatureKnowledge.completion);
			knowledgeList.appendTag(knowledgeEntry);
		}
		nbtTagCompound.setTag("CreatureKnowledge", knowledgeList);
    }
}
