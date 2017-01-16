package lycanite.lycanitesmobs.freshwatermobs.item;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;

public class ItemFreshwaterEgg extends ItemCustomSpawnEgg {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFreshwaterEgg() {
        super();
        setUnlocalizedName("freshwaterspawn");
        this.group = FreshwaterMobs.group;
        this.itemName = "freshwaterspawn";
        this.texturePath = "freshwaterspawn";
    }
}
