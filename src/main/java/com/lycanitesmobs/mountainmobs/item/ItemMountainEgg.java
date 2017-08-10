package com.lycanitesmobs.mountainmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.mountainmobs.MountainMobs;

public class ItemMountainEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMountainEgg() {
        super();
        setUnlocalizedName("mountainspawn");
        this.group = MountainMobs.group;
        this.itemName = "mountainspawn";
        this.texturePath = "mountainspawn";
    }
}
