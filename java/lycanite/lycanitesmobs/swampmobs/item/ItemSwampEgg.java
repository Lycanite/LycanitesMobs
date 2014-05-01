package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;

public class ItemSwampEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwampEgg() {
        super();
        setUnlocalizedName("SwampSpawnEgg");
        this.mod = SwampMobs.instance;
        this.itemName = "SwampEgg";
        this.texturePath = "swampspawn";
    }
}
