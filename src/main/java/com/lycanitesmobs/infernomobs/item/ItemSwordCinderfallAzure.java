package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordCinderfallAzure extends ItemSwordCinderfall {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfallAzure() {
        super();
    	this.itemName = "azurecinderfallsword";
        this.setup();
        this.textureName = "swordcinderfallazure";
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    public void onSpawnEntity(Entity entity) {
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.setSubspecies(1, true);
            entityCreature.setTemporary(40 * 20);
        }
    }


    // ==================================================
    //                     Tool/Weapon
    // ==================================================
    // ========== Get Sword Damage ==========
    @Override
    public float getDamageVsEntity() {
        return 4F;
    }
}
