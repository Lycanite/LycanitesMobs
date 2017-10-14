package com.lycanitesmobs.forestmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.forestmobs.ForestMobs;

public class ItemForestEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemForestEgg() {
        super();
        setUnlocalizedName("forestspawn");
        this.group = ForestMobs.instance.group;
        this.itemName = "forestspawn";
        this.texturePath = "forestspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
