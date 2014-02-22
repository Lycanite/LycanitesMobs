package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;

public class ItemArcticEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcticEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("ArcticSpawnEgg");
        this.mod = ArcticMobs.instance;
        this.itemName = "ArcticEgg";
        this.texturePath = "arcticspawn";
    }
}
