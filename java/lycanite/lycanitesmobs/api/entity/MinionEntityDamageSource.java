package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class MinionEntityDamageSource extends EntityDamageSource {
    EntityDamageSource minionDamageSource;
	
    // ==================================================
  	//                     Constructor
  	// ==================================================
	public MinionEntityDamageSource(EntityDamageSource minionDamageSource, Entity owner) {
		super(minionDamageSource.damageType, owner);
        this.minionDamageSource = minionDamageSource;
	}
}
