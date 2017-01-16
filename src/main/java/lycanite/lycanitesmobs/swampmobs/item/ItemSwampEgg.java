package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;

public class ItemSwampEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwampEgg() {
        super();
        setUnlocalizedName("swampspawn");
        this.group = SwampMobs.group;
        this.itemName = "swampspawn";
        this.texturePath = "swampspawn";
    }
}
