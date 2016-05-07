package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordVenomAxebladeGolden extends ItemSwordVenomAxeblade {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordVenomAxebladeGolden() {
        super();
    	this.itemName = "goldenvenomaxeblade";
        this.setup();
        this.textureName = "swordvenomaxebladegolden";
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
