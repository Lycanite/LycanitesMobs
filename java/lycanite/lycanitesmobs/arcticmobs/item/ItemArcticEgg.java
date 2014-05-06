package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;

public class ItemArcticEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcticEgg() {
        super();
        setUnlocalizedName("arcticspawn");
        this.mod = ArcticMobs.instance;
        this.itemName = "arcticspawn";
        this.texturePath = "arcticspawn";
    }
}
