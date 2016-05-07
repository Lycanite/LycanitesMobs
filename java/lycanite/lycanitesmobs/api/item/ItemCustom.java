package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;

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
