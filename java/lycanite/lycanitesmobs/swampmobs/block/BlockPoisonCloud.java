package lycanite.lycanitesmobs.swampmobs.block;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPoisonCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPoisonCloud() {
		super(Material.air);
		
		// Properties:
		this.mod = SwampMobs.instance;
		this.blockName = "PoisonCloud";
		this.setup();
		
		// Stats:
		this.tickRate = this.mod.getConfig().getFeatureBool("PoisonCloud") ? 200 : 1;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = true;
		
		this.noEntityCollision = true;
		this.noBreakCollision = true;
		this.isOpaque = false;
		
		this.setBlockUnbreakable();
		this.setLightOpacity(1);
	}


	// ==================================================
	//                     Break
	// ==================================================
	@Override
	public Item getItemDropped(int metadata, Random random, int fortune) {
		return ObjectManager.getItem("PoisonGland");
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
		if(entity instanceof EntityLivingBase) {
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.id, 5 * 20, 0));
		}
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if(par5Random.nextInt(24) == 0)
            par1World.playSound((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), AssetManager.getSound("PoisonCloud"), 1.0F + par5Random.nextFloat(), par5Random.nextFloat() * 0.7F + 0.3F, false);

        int l;
        float f;
        float f1;
        float f2;

        for(l = 0; l < 12; ++l) {
            f = (float)par2 + par5Random.nextFloat();
            f1 = (float)par3 + par5Random.nextFloat() * 0.5F;
            f2 = (float)par4 + par5Random.nextFloat();
            //TODO EntityParticle particle = new EntityParticle(par1World, f, f1, f2, "PoisonCloud", this.mod);
            par1World.spawnParticle("portal", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("smoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.blockName, this.mod.getDomain(), this.getTextureName(), iconRegister);
    }
    
    // ========== Get Icon from Side and Meta ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int par1, int par2) {
        return null;
    }

    // ========== Get Render Type ==========
    @Override
    public int getRenderType() {
        return -1;
    }
}
