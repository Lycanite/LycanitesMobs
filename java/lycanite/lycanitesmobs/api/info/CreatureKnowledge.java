package lycanite.lycanitesmobs.api.info;

import net.minecraft.entity.player.EntityPlayer;

public class CreatureKnowledge {
	public EntityPlayer player;
	public String creatureName;
	public double completion = 0;
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public CreatureKnowledge(EntityPlayer player, String creatureName, double completion) {
		this.player = player;
		this.creatureName = creatureName;
		this.completion = completion;
	}
}
