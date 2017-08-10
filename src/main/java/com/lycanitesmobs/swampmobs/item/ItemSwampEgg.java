package com.lycanitesmobs.swampmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.swampmobs.SwampMobs;

public class ItemSwampEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwampEgg() {
        super();
        setUnlocalizedName("swampspawn");
        this.group = SwampMobs.group;
        this.itemName = "swampspawn";
        this.texturePath = "swampspawn";
    }
}
