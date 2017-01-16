package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordCinderfallVerdant extends ItemSwordCinderfall {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfallVerdant() {
        super();
    	this.itemName = "verdantcinderfallsword";
        this.setup();
        this.textureName = "swordcinderfallverdant";
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
