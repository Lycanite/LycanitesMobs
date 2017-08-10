package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.GroupInfo;

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
