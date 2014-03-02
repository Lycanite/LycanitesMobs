package lycanite.lycanitesmobs;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;

public class EventListener {
	public Minecraft mc;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	
    // ==================================================
    //                 Entity Spawn Event
    // ==================================================
	@ForgeSubscribe
	public void onEntitySpawned(SpecialSpawn event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		//if(event.entityLiving != null && event.entityLiving instanceof EntityCreatureBase)
			//((EntityCreatureBase)event.entityLiving).onSpawn();
		// This doesn't seem to work. :/
	}
}
