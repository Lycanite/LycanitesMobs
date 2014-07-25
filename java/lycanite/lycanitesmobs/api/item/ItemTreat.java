package lycanite.lycanitesmobs.api.item;


public class ItemTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTreat(String setItemName, String setDomain) {
        super();
		this.itemName = setItemName;
		this.domain = setDomain;
        this.setMaxStackSize(1);
        this.textureName = this.itemName.toLowerCase();
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }
}
