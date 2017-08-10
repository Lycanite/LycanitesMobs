package com.lycanitesmobs.plainsmobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.plainsmobs.PlainsMobs;

public class ItemPlainsEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPlainsEgg() {
        super();
        setUnlocalizedName("plainsspawn");
        this.group = PlainsMobs.group;
        this.itemName = "plainsspawn";
        this.texturePath = "plainsspawn";
    }
}
