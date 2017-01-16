package lycanite.lycanitesmobs.plainsmobs.item;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;

public class ItemPlainsEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPlainsEgg() {
        super();
        setUnlocalizedName("plainsspawn");
        this.group = PlainsMobs.group;
        this.itemName = "plainsspawn";
        this.texturePath = "plainsspawn";
    }
}
