package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.LycanitesMobs;

public class ItemCustom extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemCustom(String itemName, GroupInfo group) {
        super();
        this.itemName = itemName;
        this.group = group;
        this.setCreativeTab(LycanitesMobs.itemsTab);
        this.setup();
    }
}
