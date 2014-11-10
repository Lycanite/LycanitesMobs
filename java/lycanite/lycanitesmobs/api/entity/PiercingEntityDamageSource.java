package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class PiercingEntityDamageSource extends EntityDamageSource {
	
    // ==================================================
  	//                     Constructor
  	// ==================================================
	public PiercingEntityDamageSource(String damageType, Entity entity) {
		super(damageType, entity);
		this.setDamageBypassesArmor();
		this.setDamageIsAbsolute();
	}
}
