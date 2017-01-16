package lycanite.lycanitesmobs.core.block;

import net.minecraft.block.BlockStairs;
import net.minecraft.util.ResourceLocation;

public class BlockStairsCustom extends BlockStairs {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockStairsCustom(BlockBase block) {
		super(block.getDefaultState());
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + "_stairs"));
        this.setUnlocalizedName(block.blockName + "_stairs");
	}
}
