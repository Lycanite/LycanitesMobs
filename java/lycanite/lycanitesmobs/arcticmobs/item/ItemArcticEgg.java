package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;

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
