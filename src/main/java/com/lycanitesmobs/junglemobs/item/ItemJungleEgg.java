package com.lycanitesmobs.junglemobs.item;

import com.lycanitesmobs.junglemobs.JungleMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

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
