package lycanite.lycanitesmobs.plainsmobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;

public class ItemPlainsEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPlainsEgg() {
        super();
        setUnlocalizedName("PlainsSpawnEgg");
        this.mod = PlainsMobs.instance;
        this.itemName = "PlainsEgg";
        this.texturePath = "plainsspawn";
    }
}
