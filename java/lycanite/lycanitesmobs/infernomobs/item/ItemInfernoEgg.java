package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;

public class ItemInfernoEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemInfernoEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("InfernoSpawnEgg");
        this.mod = InfernoMobs.instance;
        this.itemName = "InfernoEgg";
        this.texturePath = "infernospawn";
    }
}
