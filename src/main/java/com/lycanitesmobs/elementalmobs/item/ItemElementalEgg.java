package com.lycanitesmobs.elementalmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.elementalmobs.ElementalMobs;

public class ItemElementalEgg extends ItemCustomSpawnEgg {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemElementalEgg() {
        super();
        this.setUnlocalizedName("elementalspawn");
        this.group = ElementalMobs.instance.group;
        this.itemName = "elementalspawn";
        this.texturePath = "elementalspawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
