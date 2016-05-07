package lycanite.lycanitesmobs.junglemobs.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import lycanite.lycanitesmobs.junglemobs.entity.EntityVespidQueen;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

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
		this.tickRate = 100;
		this.removeOnTick = true;
	}
    
    
	// ==================================================
	//                   Placement
	// ==================================================
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        int orientationMeta = BlockPistonBase.determineOrientation(world, x, y, z, entityLiving);
        orientationMeta += 8;
        world.setBlockMetadataWithNotify(x, y, z, orientationMeta, 2);
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
    }
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(World world) {
    	return this.tickRate + world.rand.nextInt(100);
    }

    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
		if(world.isRemote)
			return;
		if(world.getBlockMetadata(x, y, z) >= 8)
			return;
		double range = 32D;
		if(!world.getEntitiesWithinAABB(EntityVespidQueen.class, AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range)).isEmpty())
			return;		
		super.updateTick(world, x, y, z, random);
    }
}
