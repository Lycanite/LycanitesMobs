package com.lycanitesmobs.desertmobs.item;

import com.lycanitesmobs.desertmobs.DesertMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemDesertEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemDesertEgg() {
        super();
        setUnlocalizedName("desertspawn");
        this.group = DesertMobs.group;
        this.itemName = "desertspawn";
        this.texturePath = "desertspawn";
    }
}
