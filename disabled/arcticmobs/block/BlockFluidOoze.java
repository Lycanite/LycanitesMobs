package lycanite.lycanitesmobs.arcticmobs.block;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFluidOoze extends BlockFluidClassic {
	public String blockName;
	public GroupInfo group;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidOoze(Fluid fluid) {
		super(fluid, Material.water);
		this.blockName = "ooze";
        this.setBlockName(this.blockName);
		this.group = ArcticMobs.group;
		this.setRenderPass(0);

        this.setLightOpacity(0);
        this.setLightLevel(0.25F);
	}
    
    
	// ==================================================
	//                       Fluid
	// ==================================================
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		
		// Freeze Water:
		if(block == Blocks.water) {
            if(world instanceof World) {
                ((World)world).setBlock(x, y, z, Blocks.ice, 0, 3);
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
	//                      Collision
	// ==================================================
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if(entity != null) {
            // Damage:
            if (!(entity instanceof EntityItem)) {
                entity.attackEntityFrom(ObjectManager.getDamageSource("ooze"), 1F);
            }

            // Extinguish:
            if(entity.isBurning())
                entity.extinguish();

            // Effects:
            if(entity instanceof EntityLivingBase) {
                ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 5 * 20, 0));
                ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.hunger.id, 5 * 20, 0));
            }
        }
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    	int l;
        float f; 
        float f1;
        float f2;
        
        if (random.nextInt(100) == 0) {
	        f = (float)x + random.nextFloat();
	        f1 = (float)y + random.nextFloat() * 0.5F;
	        f2 = (float)z + random.nextFloat();
	        world.spawnParticle("snowshovel", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
        super.randomDisplayTick(world, x, y, z, random);
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addSprite(this.blockName + "_still", this.group, this.blockName + "_still", iconRegister);
    	AssetManager.addSprite(this.blockName + "_flow", this.group, this.blockName + "_flow", iconRegister);
    }
    
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
    	return (side == 0 || side == 1) ? AssetManager.getSprite(this.blockName + "_still") : AssetManager.getSprite(this.blockName + "_flow");
    }
}
