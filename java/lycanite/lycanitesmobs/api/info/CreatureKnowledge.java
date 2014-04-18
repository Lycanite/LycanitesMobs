package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.entity.player.EntityPlayer;

public class CreatureKnowledge {
	public EntityPlayer player;
	public String creatureName;
	public MobInfo creatureInfo;
	public double completion = 0;
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public CreatureKnowledge(EntityPlayer player, String creatureName, double completion) {
		this.player = player;
		this.creatureName = creatureName;
		this.creatureInfo = MobInfo.mobClassToInfo.get(ObjectManager.getMob(creatureName));
		this.completion = completion;
	}
}
