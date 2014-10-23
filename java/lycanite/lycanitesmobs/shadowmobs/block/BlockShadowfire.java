package lycanite.lycanitesmobs.shadowmobs.block;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ClientProxy;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockShadowfire extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockShadowfire() {
		super(Material.fire);
		
		// Properties:
		this.group = ShadowMobs.group;
		this.blockName = "shadowfire";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Shadowfire", true) ? 200 : 1;
		this.removeOnTick = false;
		this.loopTicks = true;
		this.canBeCrushed = true;
		
		this.noEntityCollision = true;
		this.noBreakCollision = false;
		this.isOpaque = false;
		
		this.setLightOpacity(1);
	}
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
	// ========== Tick Rate ==========
    @Override
    public int tickRate(World par1World) {
        return 30;
    }
    
    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
    	// ========== Main Fire Logic ==========
		if(!ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Shadowfire"))
    		world.setBlockToAir(x, y, z);
		
		Block base = world.getBlock(x, y - 1, z);
		
		
        boolean onFireFuel = (base == Blocks.obsidian);

        if(!this.canPlaceBlockAt(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
        }
        
        
        // ========== Base Logic ==========
		super.updateTick(world, x, y, z, random);
		
		
		// ========== Main Fire Logic Continued ==========
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata < 15) {
            world.setBlockMetadataWithNotify(x, y, z, metadata + random.nextInt(3) / 2, 4);
        }
        
		// Turn to air if no neighbor blocks can burn and this block is not on a solid block/water.
        if(!onFireFuel && !this.canNeighborBurn(world, x, y, z)) {
            if((!world.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && base != Blocks.water) || metadata > 3) {
                world.setBlockToAir(x, y, z);
                return;
            }
        }
        
        // Random Chance of Fizzling Out (Uses metadata):
        if(!onFireFuel && metadata == 15 && random.nextInt(4) == 0) {
            world.setBlockToAir(x, y, z);
            return;
        }
    }
    
    // ========== High Update Priority ==========
    // Setting this to true could cause lag if there's a lot of fire!
    @Override
    public boolean func_149698_L() {
    	return false;
    }


	// ==================================================
	//                       Fire
	// ==================================================
    // ========== Can Block Burn ==========
    public boolean canBlockBurn(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        Block block = world.getBlock(x, y, z);
    	if(block == Blocks.snow)
        	return true;
        if(block == Blocks.snow_layer && face != ForgeDirection.UP && face != ForgeDirection.DOWN)
        	return true;
        if(block instanceof BlockIce)
        	return true;
        if(block instanceof BlockPackedIce)
        	return true;
    	return false;
    }
    
    // ========== Can Neighbor Burn ==========
    public boolean canNeighborBurn(World world, int x, int y, int z) {
        return this.canBlockBurn(world, x + 1, y, z, ForgeDirection.WEST ) ||
               this.canBlockBurn(world, x - 1, y, z, ForgeDirection.EAST ) ||
               this.canBlockBurn(world, x, y - 1, z, ForgeDirection.UP   ) ||
               this.canBlockBurn(world, x, y + 1, z, ForgeDirection.DOWN ) ||
               this.canBlockBurn(world, x, y, z - 1, ForgeDirection.SOUTH) ||
               this.canBlockBurn(world, x, y, z + 1, ForgeDirection.NORTH);
    }
    
    // ========== Try To Catch Block On Fire ===========
    private void tryCatchFire(World world, int x, int y, int z, int chance, Random random, int metadata, ForgeDirection face) {
        int j1 = 0;
        Block block = world.getBlock(x, y, z);
        if(block != null) {
            j1 = 0;
        }

        if(random.nextInt(chance) < j1 / 8) {
            if(random.nextInt(metadata + 10) < 5) {
                int k1 = metadata + random.nextInt(5) / 4;

                if(k1 > 15) {
                    k1 = 15;
                }

                world.setBlock(x, y, z, this, k1, 3);
            }
            else {
            	world.setBlockToAir(x, y, z);
            }
        }
    }
    
    // ========== Get Chance of Neighbors Encouraging Fire ==========
    public int getChanceOfNeighborsEncouragingFire(World world, int x, int y, int z) {
        byte initialChance = 0;
        if(!world.isAirBlock(x, y, z))
            return 0;
        
        int fireChance = initialChance;
        fireChance = this.getChanceToEncourageFire(world, x + 1, y, z, fireChance, WEST );
        fireChance = this.getChanceToEncourageFire(world, x - 1, y, z, fireChance, EAST );
        fireChance = this.getChanceToEncourageFire(world, x, y - 1, z, fireChance, UP   );
        fireChance = this.getChanceToEncourageFire(world, x, y + 1, z, fireChance, DOWN );
        fireChance = this.getChanceToEncourageFire(world, x, y, z - 1, fireChance, SOUTH);
        fireChance = this.getChanceToEncourageFire(world, x, y, z + 1, fireChance, NORTH);
        return fireChance;
    }

    // ========== Get Chance To Encoure Fire ==========
    public int getChanceToEncourageFire(IBlockAccess world, int x, int y, int z, int oldChance, ForgeDirection face) {
    	int newChance = 0;
    	if(world.getBlock(x, y, z) == Blocks.snow)
    		newChance = 50;
    	if(this.canBlockBurn(world, x, y, z, face))
    		newChance = 10;
        return (newChance > oldChance ? newChance : oldChance);
    }
    

	// ==================================================
	//                       Break
	// ==================================================
	@Override
	public Item getItemDropped(int metadata, Random random, int fortune) {
		return ObjectManager.getItem("spectralboltcharge");
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
		
		if(entity instanceof EntityItem) // Shadowfire shouldn't destroy items.
    		return;
		
		PotionEffect effectWither = new PotionEffect(Potion.blindness.id, 5 * 20, 0);
		PotionEffect effectFear = null;
		if(ObjectManager.getPotionEffect("fear") != null)
			effectFear = new PotionEffect(ObjectManager.getPotionEffect("fear").id, 5 * 20, 0); // Not applied, used to check for immunity only.
		if(entity instanceof EntityLivingBase) {
			EntityLivingBase entityLiving = (EntityLivingBase)entity;
			if(!entityLiving.isPotionApplicable(effectWither) && (effectFear == null || !entityLiving.isPotionApplicable(effectFear)))
				return; // Entities immune to both are normally shadow mobs.
			entityLiving.addPotionEffect(effectWither);
		}
		
    	entity.attackEntityFrom(DamageSource.magic, 2);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    	if(random.nextInt(24) == 0)
        	world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("shadowfire"), 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
    	
        int l;
        float f;
        float f1;
        float f2;

        for(l = 0; l < 12; ++l) {
            f = (float)x + random.nextFloat();
            f1 = (float)y + random.nextFloat() * 0.5F;
            f2 = (float)z + random.nextFloat();
            //TODO EntityParticle particle = new EntityParticle(world, f, f1, f2, "shadowfire", this.mod);
            world.spawnParticle("witchMagic", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addIconGroup(blockName, this.group, new String[] {"shadowfire_layer_0", "shadowfire_layer_1"}, iconRegister);
    }
    
    // ========== Get Icon from Side and Meta ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int par1, int par2) {
        return AssetManager.getIconGroup(blockName)[0];
    }

    // ========== Get Render Type ==========
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ClientProxy.RENDER_ID;
    }
    
    // ========== Render As Normal ==========
 	@Override
 	public boolean renderAsNormalBlock() {
 		return false;
 	}
}