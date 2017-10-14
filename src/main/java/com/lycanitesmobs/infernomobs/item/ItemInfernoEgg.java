package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;

public class ItemInfernoEgg extends ItemCustomSpawnEgg {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemInfernoEgg() {
        super();
        setUnlocalizedName("infernospawn");
        this.group = InfernoMobs.instance.group;
        this.itemName = "infernospawn";
        this.texturePath = "infernospawn";
        this.setRegistryName(this.group.filename, this.itemName);
    }
}
