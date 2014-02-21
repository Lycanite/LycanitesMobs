package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;

public class ItemDemonEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDemonEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("DemonSpawnEgg");
        this.mod = DemonMobs.instance;
        this.itemName = "DemonEgg";
        this.texturePath = "demonspawn";
    }
}
