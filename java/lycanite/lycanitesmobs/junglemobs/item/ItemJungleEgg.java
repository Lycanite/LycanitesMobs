package lycanite.lycanitesmobs.junglemobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;

public class ItemJungleEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemJungleEgg(int itemID) {
        super(itemID);
        setUnlocalizedName("JungleSpawnEgg");
        this.mod = JungleMobs.instance;
        this.itemName = "JungleEgg";
        this.texturePath = "junglespawn";
    }
}
