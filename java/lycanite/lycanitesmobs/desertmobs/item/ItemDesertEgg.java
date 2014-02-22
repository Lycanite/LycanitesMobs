package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;

public class ItemDesertEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDesertEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("DesertSpawnEgg");
        this.mod = DesertMobs.instance;
        this.itemName = "DesertEgg";
        this.texturePath = "desertspawn";
    }
}
