package lycanite.lycanitesmobs.mountainmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;

public class ItemMountainEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMountainEgg() {
        super();
        setUnlocalizedName("MountainSpawnEgg");
        this.mod = MountainMobs.instance;
        this.itemName = "MountainEgg";
        this.texturePath = "mountainspawn";
    }
}
