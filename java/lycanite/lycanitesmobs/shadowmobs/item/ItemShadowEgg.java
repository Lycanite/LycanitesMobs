package lycanite.lycanitesmobs.shadowmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;

public class ItemShadowEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemShadowEgg() {
        super();
        setUnlocalizedName("mountainspawn");
        this.group = ShadowMobs.group;
        this.itemName = "mountainspawn";
        this.texturePath = "mountainspawn";
    }
}
