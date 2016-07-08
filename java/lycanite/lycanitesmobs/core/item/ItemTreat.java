package lycanite.lycanitesmobs.core.item;

import lycanite.lycanitesmobs.core.info.GroupInfo;


public class ItemTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTreat(String setItemName, GroupInfo group) {
        super();
		this.itemName = setItemName;
		this.group = group;
        this.setMaxStackSize(16);
        this.textureName = this.itemName.toLowerCase();
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }
}
