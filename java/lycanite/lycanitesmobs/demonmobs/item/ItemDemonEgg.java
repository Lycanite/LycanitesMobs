package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;

public class ItemDemonEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonEgg() {
        super();
        setUnlocalizedName("demonspawn");
        this.mod = DemonMobs.instance;
        this.itemName = "demonspawn";
        this.texturePath = "demonspawn";
    }
}
