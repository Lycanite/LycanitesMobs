package lycanite.lycanitesmobs.infernomobs.block;

import java.util.ArrayList;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidPureLava extends BlockFluidClassic {
	public String blockName;
	public ILycaniteMod mod;
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidPureLava(Fluid fluid) {
		super(fluid, Material.lava);
		this.blockName = "purelava";
        this.setBlockName(this.blockName);
		this.mod = InfernoMobs.instance;
		this.setRenderPass(1);
	}
    
    
	// ==================================================
	//                       Fluid
	// ==================================================
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		
		// Renewable Fluid:
		if(block == this) {
			BlockFluidClassic fluidBlock = (BlockFluidClassic)block;
			if(world.getBlockMetadata(x, y, z) != 0) {
				byte otherSourceBlocks = 0;
				ArrayList<int[]> adjBlockCoords = new ArrayList<int[]>();
				adjBlockCoords.add(new int[] {x - 1, y, z});
				adjBlockCoords.add(new int[] {x + 1, y, z});
				adjBlockCoords.add(new int[] {x, y + 1, z});
				adjBlockCoords.add(new int[] {x, y, z - 1});
				adjBlockCoords.add(new int[] {x, y, z + 1});
				for(int[] adjBlockCoord : adjBlockCoords) {
					Block adjBlock = world.getBlock(adjBlockCoord[0], adjBlockCoord[1], adjBlockCoord[2]);
					int adjMetadata = world.getBlockMetadata(adjBlockCoord[0], adjBlockCoord[1], adjBlockCoord[2]);
					if(adjBlock == this && adjMetadata == 0)
						otherSourceBlocks++;
					if(otherSourceBlocks > 1)
						break;
				}
				
				if(otherSourceBlocks > 1) {
					if(world instanceof World) {
						((World)world).setBlock(x, y, z, this, 0, 3);
					}
				}
			}
			return false;
		}
		
		if(block.getMaterial().isLiquid()) return false;
		return super.canDisplace(world, x, y, z);
	}
	
	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if(world.getBlock(x, y, z).getMaterial().isLiquid()) return this.canDisplace(world, x, y, z);
		return super.displaceIfPossible(world, x, y, z);
	}
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.blockName + "_still", this.mod.getDomain(), this.blockName + "_still", iconRegister);
    	AssetManager.addIcon(this.blockName + "_flow", this.mod.getDomain(), this.blockName + "_flow", iconRegister);
    }
    
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
    	return (side == 0 || side == 1) ? AssetManager.getIcon(this.blockName + "_still") : AssetManager.getIcon(this.blockName + "_flow");
    }
}
