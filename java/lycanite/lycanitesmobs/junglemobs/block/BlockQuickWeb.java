package lycanite.lycanitesmobs.junglemobs.block;

import java.util.Random;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class BlockQuickWeb extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockQuickWeb(int blockID) {
		super(blockID, Material.web);
		
		// Properties:
		this.mod = JungleMobs.instance;
		this.blockName = "QuickWeb";
		this.setup();
		
		// Stats:
		this.tickRate = this.mod.getConfig().getFeatureBool("QuickWeb") ? 200 : 1;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = false;
		
		this.noEntityCollision = true;
		this.noBreakCollision = false;
		this.isOpaque = false;
		
		this.setHardness(4.0F);
		this.setLightOpacity(1);
	}


	// ==================================================
	//                     Break
	// ==================================================
	@Override
	public int idDropped(int metadata, Random random, int fortune) {
		return ObjectManager.getItem("QuickWebCharge").itemID;
	}
	
	@Override
	public int damageDropped(int metadata) {
		return 0;
	}
    
	@Override
	public int quantityDropped(Random par1Random) {
        return 0;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		entity.setInWeb();
	}
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Get Render Type ==========
    @Override
    public int getRenderType() {
        return BlockBase.RENDER_TYPE.CROSS.id;
    }
    
    // ========== Render As Normal ==========
 	@Override
 	public boolean renderAsNormalBlock() {
 		return false;
 	}
}
