package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;

public class ItemForestEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemForestEgg() {
        super();
        setUnlocalizedName("forestspawn");
        this.group = ForestMobs.group;
        this.itemName = "forestspawn";
        this.texturePath = "forestspawn";
    }
}
