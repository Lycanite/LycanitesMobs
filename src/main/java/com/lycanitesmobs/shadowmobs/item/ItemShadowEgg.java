package com.lycanitesmobs.shadowmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.shadowmobs.ShadowMobs;

public class ItemShadowEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemShadowEgg() {
        super();
        setUnlocalizedName("shadowspawn");
        this.group = ShadowMobs.instance.group;
        this.itemName = "shadowspawn";
        this.texturePath = "shadowspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
