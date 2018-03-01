package com.lycanitesmobs.elementalmobs.item;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordCinderfallVerdant extends ItemSwordCinderfall {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfallVerdant(String itemName, String textureName) {
        super(itemName, textureName);
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
    public float getAttackDamage() {
        return 4F;
    }
}
