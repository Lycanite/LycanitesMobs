package lycanite.lycanitesmobs.junglemobs.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockVeswax extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockVeswax() {
		super(Material.wood);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = JungleMobs.group;
		this.blockName = "veswax";
		this.setup();
		
		// Stats:
		this.setHardness(0.6F);
		this.setHarvestLevel("axe", 0);
		this.setStepSound(this.soundTypeWood);
	}
    
    
	// ==================================================
	//                   Placement
	// ==================================================
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        int orientationMeta = BlockPistonBase.determineOrientation(world, x, y, z, entityLiving);
        world.setBlockMetadataWithNotify(x, y, z, orientationMeta, 2);
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
    }
}
