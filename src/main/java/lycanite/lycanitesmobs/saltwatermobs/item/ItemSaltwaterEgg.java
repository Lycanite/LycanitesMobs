package lycanite.lycanitesmobs.saltwatermobs.item;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.saltwatermobs.SaltwaterMobs;

public class ItemSaltwaterEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSaltwaterEgg() {
        super();
        setUnlocalizedName("saltwaterspawn");
        this.group = SaltwaterMobs.group;
        this.itemName = "saltwaterspawn";
        this.texturePath = "saltwaterspawn";
    }
}
