package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.arcticmobs.ArcticMobs;

public class ItemArcticEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcticEgg() {
        super();
        setUnlocalizedName("arcticspawn");
        this.group = ArcticMobs.group;
        this.itemName = "arcticspawn";
        this.texturePath = "arcticspawn";
    }
}
