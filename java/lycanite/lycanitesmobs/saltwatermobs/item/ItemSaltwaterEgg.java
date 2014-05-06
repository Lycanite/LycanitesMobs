package lycanite.lycanitesmobs.saltwatermobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.saltwatermobs.SaltwaterMobs;

public class ItemSaltwaterEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSaltwaterEgg() {
        super();
        setUnlocalizedName("saltwaterspawn");
        this.mod = SaltwaterMobs.instance;
        this.itemName = "saltwaterspawn";
        this.texturePath = "saltwaterspawn";
    }
}
