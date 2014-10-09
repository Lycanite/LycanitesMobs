package lycanite.lycanitesmobs.shadowmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;

public class ItemShadowEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemShadowEgg() {
        super();
        setUnlocalizedName("shadowspawn");
        this.group = ShadowMobs.group;
        this.itemName = "shadowspawn";
        this.texturePath = "shadowspawn";
    }
}
