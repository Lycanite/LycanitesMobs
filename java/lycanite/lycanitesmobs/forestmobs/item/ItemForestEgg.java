package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;

public class ItemForestEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemForestEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("ForestSpawnEgg");
        this.mod = ForestMobs.instance;
        this.itemName = "ForestEgg";
        this.texturePath = "forestspawn";
    }
}
