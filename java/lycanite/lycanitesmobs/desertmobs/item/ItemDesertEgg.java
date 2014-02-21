package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;

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
