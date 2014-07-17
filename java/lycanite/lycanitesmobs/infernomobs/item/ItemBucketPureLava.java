package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBucketPureLava extends ItemBucket {
	public String itemName;
	public ILycaniteMod mod;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBucketPureLava() {
        super(ObjectManager.getBlock("purelava"));
		this.setCreativeTab(LycanitesMobs.itemsTab);
        this.mod = InfernoMobs.instance;
        this.itemName = "bucketpurelava";
        this.setUnlocalizedName(this.itemName);
        ObjectManager.addBucket(this, ObjectManager.getBlock("purelava"));
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
    	return AssetManager.getIcon(this.itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.itemName, this.mod.getDomain(), this.itemName, iconRegister);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
