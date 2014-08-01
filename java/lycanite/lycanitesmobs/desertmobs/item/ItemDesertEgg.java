package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;

public class ItemDesertEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDesertEgg() {
        super();
        setUnlocalizedName("desertspawn");
        this.group = DesertMobs.group;
        this.itemName = "desertspawn";
        this.texturePath = "desertspawn";
    }
}
