package lycanite.lycanitesmobs;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class EventListener {
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}
	
	
    // ==================================================
    //                 Attack Target Event
    // ==================================================
	@ForgeSubscribe
	public void onAttackTarget(LivingSetAttackTargetEvent event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		// Better Invisibility:
		if(event.entityLiving != null) {
			if(event.entityLiving.isPotionActive(Potion.nightVision))
				return;
			if(event.target != null) {
				if(event.target.isInvisible())
					if(event.isCancelable())
						event.setCanceled(true);
			}
		}
	}
}
