package lycanite.lycanitesmobs.junglemobs.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockPropolis extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPropolis() {
		super(Material.clay);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = JungleMobs.group;
		this.blockName = "propolis";
		this.setup();
		
		// Stats:
		this.setHardness(0.6F);
		this.setHarvestLevel("shovel", 0);
		this.setStepSound(this.soundTypeGravel);
	}
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		// Possible slowness?
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
