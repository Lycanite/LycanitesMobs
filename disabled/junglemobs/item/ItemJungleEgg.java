package lycanite.lycanitesmobs.junglemobs.item;

import lycanite.lycanitesmobs.api.item.ItemCustomSpawnEgg;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;

public class ItemJungleEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemJungleEgg() {
        super();
        setUnlocalizedName("junglespawn");
        this.group = JungleMobs.group;
        this.itemName = "junglespawn";
        this.texturePath = "junglespawn";
    }
}
