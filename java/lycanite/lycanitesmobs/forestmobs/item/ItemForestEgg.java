package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;

public class ItemForestEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemForestEgg() {
        super();
        setUnlocalizedName("ForestSpawnEgg");
        this.mod = ForestMobs.instance;
        this.itemName = "ForestEgg";
        this.texturePath = "forestspawn";
    }
}
