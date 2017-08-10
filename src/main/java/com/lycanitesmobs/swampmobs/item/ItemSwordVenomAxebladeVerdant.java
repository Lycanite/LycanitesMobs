package com.lycanitesmobs.swampmobs.item;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordVenomAxebladeVerdant extends ItemSwordVenomAxeblade {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordVenomAxebladeVerdant() {
        super();
    	this.itemName = "verdantvenomaxeblade";
        this.setup();
        this.textureName = "swordvenomaxebladeverdant";
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    @Override
    public void onSpawnEntity(Entity entity) {
        super.onSpawnEntity(entity);
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.setSubspecies(2, true);
        }
    }

    @Override
    public float getSpecialEffectChance() { return 0.4F; }


    // ==================================================
    //                     Tool/Weapon
    // ==================================================
    // ========== Get Sword Damage ==========
    @Override
    public float getDamageVsEntity() {
        return 4F;
    }
}
