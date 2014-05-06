package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;

public class ItemInfernoEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemInfernoEgg() {
        super();
        setUnlocalizedName("infernospawn");
        this.mod = InfernoMobs.instance;
        this.itemName = "infernospawn";
        this.texturePath = "infernospawn";
    }
}
