package lycanite.lycanitesmobs.api.block;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBase extends Block {
	
	// Properties:
	public ILycaniteMod mod;
	public String blockName = "BlockBase";
	public String textureName = "blockbase";
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockBase(int blockID, Material material) {
		super(blockID, material);
        this.setUnlocalizedName(blockName);
        this.textureName = this.blockName.toLowerCase();
	}
	
	
	// ==================================================
	//                      Place
	// ==================================================
	
	
	
	// ==================================================
	//                      Break
	// ==================================================
	//========== Drops ==========
	@Override
	public int idDropped(int breakID, Random random, int zero) {
        return this.blockID;
	}
	
	@Override
	public int damageDropped(int breakMetadata) {
		return 0;
	}
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
    	AssetManager.addIcon(this.blockName, this.mod.getDomain(), this.textureName, iconRegister);
    }
    
    // ========== Get Icon from Side and Metadata ==========
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        return AssetManager.getIcon(blockName);
    }
    
    // ========== Render Type ==========
 	@Override
 	public boolean renderAsNormalBlock() {
 		return true;
 	}
}
