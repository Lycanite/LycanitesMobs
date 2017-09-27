package com.lycanitesmobs.freshwatermobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;

public class ItemFreshwaterEgg extends ItemCustomSpawnEgg {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFreshwaterEgg() {
        super();
        setUnlocalizedName("freshwaterspawn");
        this.group = FreshwaterMobs.group;
        this.itemName = "freshwaterspawn";
        this.texturePath = "freshwaterspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
