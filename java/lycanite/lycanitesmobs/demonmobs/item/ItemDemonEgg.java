package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;

public class ItemDemonEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonEgg() {
        super();
        setUnlocalizedName("demonspawn");
        this.group = DemonMobs.group;
        this.itemName = "demonspawn";
        this.texturePath = "demonspawn";
    }
}
