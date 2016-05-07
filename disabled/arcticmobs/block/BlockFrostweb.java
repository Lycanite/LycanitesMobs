package lycanite.lycanitesmobs.arcticmobs.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFrostweb extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFrostweb() {
		super(Material.web);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = ArcticMobs.group;
		this.blockName = "frostweb";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Frostwebs", true) ? 200 : 1;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = false;
		
		this.noEntityCollision = true;
		this.noBreakCollision = false;
		this.isOpaque = false;
		
		this.setHardness(0.1F);
		this.setHarvestLevel("sword", 0);
		this.setLightOpacity(1);
	}


	// ==================================================
	//                     Break
	// ==================================================
	@Override
	public Item getItemDropped(int breakID, Random random, int zero) {
        return ObjectManager.getItem("frostwebcharge");
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
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        int l;
        float f;
        float f1;
        float f2;

        for(l = 0; l < 12; ++l) {
            f = (float)par2 + par5Random.nextFloat();
            f1 = (float)par3 + par5Random.nextFloat();
            f2 = (float)par4 + par5Random.nextFloat();
            par1World.spawnParticle("snowshovel", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Get Render Type ==========
    @SideOnly(Side.CLIENT)
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
