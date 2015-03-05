package lycanite.lycanitesmobs.api.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;


public class ItemMobToken extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMobToken(GroupInfo group) {
        super();
		this.itemName = "Mob Token";
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
    // ========== Get Icon ==========
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack itemStack) {
        return super.getIconIndex(itemStack);
    }

    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        return;
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
