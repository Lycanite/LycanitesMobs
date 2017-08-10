package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.infernomobs.InfernoMobs;

public class ItemInfernoEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemInfernoEgg() {
        super();
        setUnlocalizedName("infernospawn");
        this.group = InfernoMobs.group;
        this.itemName = "infernospawn";
        this.texturePath = "infernospawn";
    }
}
