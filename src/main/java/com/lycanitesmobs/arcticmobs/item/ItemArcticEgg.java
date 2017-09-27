package com.lycanitesmobs.arcticmobs.item;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

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
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
