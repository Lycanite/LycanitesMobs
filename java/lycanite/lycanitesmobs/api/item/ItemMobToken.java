package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemMobToken extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMobToken(GroupInfo group) {
        super();
		this.itemName = "mobtoken";
		this.group = group;
        this.textureName = this.itemName.toLowerCase();
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }

    @Override
    public void setup() {
        this.setUnlocalizedName(this.itemName);
        this.textureName = this.itemName.toLowerCase();
        int nameLength = this.textureName.length();
        if(nameLength > 6 && this.textureName.substring(nameLength - 6, nameLength).equalsIgnoreCase("charge")) {
            this.textureName = this.textureName.substring(0, nameLength - 6);
        }
    }


    // ==================================================
    //                     Visuals
    // ==================================================
    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
