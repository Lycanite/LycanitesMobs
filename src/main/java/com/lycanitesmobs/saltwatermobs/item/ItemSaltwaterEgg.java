package com.lycanitesmobs.saltwatermobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;

public class ItemSaltwaterEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSaltwaterEgg() {
        super();
        setUnlocalizedName("saltwaterspawn");
        this.group = SaltwaterMobs.group;
        this.itemName = "saltwaterspawn";
        this.texturePath = "saltwaterspawn";
    }
}
