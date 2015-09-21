package lycanite.lycanitesmobs.api.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockSummoningPedestal extends BlockBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSummoningPedestal(GroupInfo group) {
		super(Material.iron);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = group;
		this.blockName = "summoningpedestal";
		this.setup();
		
		// Stats:
		this.setHardness(5F);
        this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
		this.setStepSound(this.soundTypeMetal);
	}


    // ==================================================
    //                      Visuals
    // ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        AssetManager.addIcon(this.blockName, this.group, this.getTextureName(), iconRegister);
        AssetManager.addIcon(this.blockName + "_side", this.group, this.getTextureName() + "_side", iconRegister);
        AssetManager.addIcon(this.blockName + "_top", this.group, this.getTextureName() + "_top", iconRegister);
    }

    // ========== Get Icon from Side and Metadata ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int metadata) {
        if(side == 0)
            return AssetManager.getIcon(blockName);
        if(side == 1)
            return AssetManager.getIcon(blockName + "_top");
        return AssetManager.getIcon(blockName + "_side");
    }
}
