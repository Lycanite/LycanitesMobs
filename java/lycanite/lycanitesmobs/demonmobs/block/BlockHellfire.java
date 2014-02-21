package lycanite.lycanitesmobs.demonmobs.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHellfire extends BlockFire {
	public static String blockName = "Hellfire";
	
    // Fire Spread:
    private int[] chanceToEncourageFire = new int[256];

    // Flammability:
    private int[] abilityToCatchFire = new int[256];
    
	// ==================================================
	//                    Constructor
	// ==================================================
    public BlockHellfire(int blockID) {
        super(blockID);
        this.setTickRandomly(true);
        this.setHardness(0.0F);
        this.setLightValue(1.0F);
        this.setStepSound(soundWoodFootstep);
        this.setUnlocalizedName("hellfire");
        this.disableStats();
    }
    
    
	// ==================================================
	//                    Initialize
	// ==================================================
    @Override
    public void initializeBlock() {
        abilityToCatchFire = Block.blockFlammability;
        chanceToEncourageFire = Block.blockFireSpreadSpeed;
    }
    
    
	// ==================================================
	//                  Get Bounding Box
	// ==================================================
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
    
    
	// ==================================================
	//                     Rendering
	// ==================================================
    // ========== Opaque ==========
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    // ========== Normal Block Render ==========
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    // ========== Get Render Type ==========
	@SideOnly(Side.CLIENT)
    public int getRenderType() {
        return 3;
    }
	
	
	// ==================================================
	//                     Drops
	// ==================================================
    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }
    
    
	// ==================================================
	//                 Damage Entities
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	if(entity instanceof EntityItem)
    		if(((EntityItem)entity).getEntityItem().itemID == ObjectManager.getItem("HellfireCharge").itemID)
    			return;
		if(entity.isImmuneToFire())
			return;
    	entity.attackEntityFrom(DamageSource.lava, 2);
		entity.setFire(5);
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
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
    	if(!DemonMobs.config.getFeatureBool("Hellfire"))
    		par1World.setBlock(par2, par3, par4, Block.fire.blockID);
        if(par1World.getGameRules().getGameRuleBooleanValue("doFireTick")) {
            Block base = Block.blocksList[par1World.getBlockId(par2, par3 - 1, par4)];
            boolean flag = (base != null && base.isFireSource(par1World, par2, par3 - 1, par4, par1World.getBlockMetadata(par2, par3 - 1, par4), UP));

            if (!this.canPlaceBlockAt(par1World, par2, par3, par4)) {
                par1World.setBlockToAir(par2, par3, par4);
            }

            if (!flag && par1World.isRaining() && (par1World.canLightningStrikeAt(par2, par3, par4) || par1World.canLightningStrikeAt(par2 - 1, par3, par4) || par1World.canLightningStrikeAt(par2 + 1, par3, par4) || par1World.canLightningStrikeAt(par2, par3, par4 - 1) || par1World.canLightningStrikeAt(par2, par3, par4 + 1))) {
                par1World.setBlockToAir(par2, par3, par4);
            }
            else {
                int l = par1World.getBlockMetadata(par2, par3, par4);

                if(l < 15) {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, l + par5Random.nextInt(3) / 2, 4);
                }

                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par5Random.nextInt(10));

                if(!flag && !this.canNeighborBurn(par1World, par2, par3, par4)) {
                    if(!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || l > 3) {
                        par1World.setBlockToAir(par2, par3, par4);
                    }
                }
                else if(!flag && !this.canBlockCatchFire(par1World, par2, par3 - 1, par4, UP) && l == 15 && par5Random.nextInt(4) == 0) {
                    par1World.setBlockToAir(par2, par3, par4);
                }
                else {
                    boolean flag1 = par1World.isBlockHighHumidity(par2, par3, par4);
                    byte b0 = 0;

                    if (flag1) {
                        b0 = -50;
                    }

                    this.tryToCatchBlockOnFire(par1World, par2 + 1, par3, par4, 300 + b0, par5Random, l, WEST );
                    this.tryToCatchBlockOnFire(par1World, par2 - 1, par3, par4, 300 + b0, par5Random, l, EAST );
                    this.tryToCatchBlockOnFire(par1World, par2, par3 - 1, par4, 250 + b0, par5Random, l, UP   );
                    this.tryToCatchBlockOnFire(par1World, par2, par3 + 1, par4, 250 + b0, par5Random, l, DOWN );
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 - 1, 300 + b0, par5Random, l, SOUTH);
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 + 1, 300 + b0, par5Random, l, NORTH);

                    for(int i1 = par2 - 1; i1 <= par2 + 1; ++i1) {
                        for(int j1 = par4 - 1; j1 <= par4 + 1; ++j1) {
                            for(int k1 = par3 - 1; k1 <= par3 + 4; ++k1) {
                                if(i1 != par2 || k1 != par3 || j1 != par4) {
                                    int l1 = 100;

                                    if(k1 > par3 + 1) {
                                        l1 += (k1 - (par3 + 1)) * 100;
                                    }

                                    int i2 = this.getChanceOfNeighborsEncouragingFire(par1World, i1, k1, j1);

                                    if(i2 > 0) {
                                        int j2 = (i2 + 40 + par1World.difficultySetting * 7) / (l + 30);

                                        if(flag1) {
                                            j2 /= 2;
                                        }

                                        if(j2 > 0 && par5Random.nextInt(l1) <= j2 && (!par1World.isRaining() || !par1World.canLightningStrikeAt(i1, k1, j1)) && !par1World.canLightningStrikeAt(i1 - 1, k1, par4) && !par1World.canLightningStrikeAt(i1 + 1, k1, j1) && !par1World.canLightningStrikeAt(i1, k1, j1 - 1) && !par1World.canLightningStrikeAt(i1, k1, j1 + 1)) {
                                            int k2 = l + par5Random.nextInt(5) / 4;

                                            if(k2 > 15) {
                                                k2 = 15;
                                            }

                                            par1World.setBlock(i1, k1, j1, this.blockID, k2, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        	par1World.setBlockToAir(par2, par3, par4);
    }
    
    
	// ==================================================
	//                    func_82506_l
	// ==================================================
    @Override
    public boolean func_82506_l() {
        return false;
    }
    
    
	// ==================================================
	//                    Spread Fire
	// ==================================================
    @Deprecated
    private void tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7) {
        tryToCatchBlockOnFire(par1World, par2, par3, par4, par5, par6Random, par7, UP);
    }
    
    private void tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7, ForgeDirection face) {
        int j1 = 0;
        Block block = Block.blocksList[par1World.getBlockId(par2, par3, par4)];
        if(block != null) {
            j1 = block.getFlammability(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), face);
        }

        if(par6Random.nextInt(par5) < j1 / 8) {
            boolean flag = par1World.getBlockId(par2, par3, par4) == Block.tnt.blockID;

            if (par6Random.nextInt(par7 + 10) < 5 && !par1World.canLightningStrikeAt(par2, par3, par4)) {
                int k1 = par7 + par6Random.nextInt(5) / 4;

                if (k1 > 15) {
                    k1 = 15;
                }

                par1World.setBlock(par2, par3, par4, this.blockID, k1, 3);
            }
            else {
                par1World.setBlockToAir(par2, par3, par4);
            }

            if(flag) {
                Block.tnt.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
            }
        }
    }
    
    
	// ==================================================
	//                  Can Neighbor Burn
	// ==================================================
    private boolean canNeighborBurn(World par1World, int par2, int par3, int par4) {
        return canBlockCatchFire(par1World, par2 + 1, par3, par4, WEST ) ||
               canBlockCatchFire(par1World, par2 - 1, par3, par4, EAST ) ||
               canBlockCatchFire(par1World, par2, par3 - 1, par4, UP   ) ||
               canBlockCatchFire(par1World, par2, par3 + 1, par4, DOWN ) ||
               canBlockCatchFire(par1World, par2, par3, par4 - 1, SOUTH) ||
               canBlockCatchFire(par1World, par2, par3, par4 + 1, NORTH);
    }
    
    
	// ==================================================
	//                 Can This Block Burn
	// ==================================================
    private int getChanceOfNeighborsEncouragingFire(World par1World, int par2, int par3, int par4) {
        byte b0 = 0;

        if (!par1World.isAirBlock(par2, par3, par4)) {
            return 0;
        }
        else {
            int l = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, b0, WEST);
            l = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, l, EAST);
            l = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, l, UP);
            l = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, l, DOWN);
            l = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, l, NORTH);
            return l;
        }
    }
    
    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    @Override
    public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block != null)
        {
            return block.isFlammable(world, x, y, z, world.getBlockMetadata(x, y, z), face);
        }
        return false;
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    @Override
    public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, ForgeDirection face)
    {
        int newChance = 0;
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block != null)
        {
            newChance = block.getFireSpreadSpeed(world, x, y, z, world.getBlockMetadata(x, y, z), face);
        }
        return (newChance > oldChance ? newChance : oldChance);
    }

    // DEP - Can Block Catch Fire
    @Deprecated
    public boolean canBlockCatchFire(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return canBlockCatchFire(par1IBlockAccess, par2, par3, par4, UP);
    }

    // DEP - Get Encourage Fire Chance
    @Deprecated
    public int getChanceToEncourageFire(World par1World, int par2, int par3, int par4, int par5)
    {
        return getChanceToEncourageFire(par1World, par2, par3, par4, par5, UP);
    }
    
    
	// ==================================================
	//                      Collision
	// ==================================================
    @Override
    public boolean isCollidable() {
        return true;
    }
    
    
	// ==================================================
	//                    Placement
	// ==================================================
    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || this.canNeighborBurn(par1World, par2, par3, par4);
    }
    
    
	// ==================================================
	//               Neighbor Block Change
	// ==================================================
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4)) {
            par1World.setBlockToAir(par2, par3, par4);
        }
    }
    
    
	// ==================================================
	//                 On Block Added
	// ==================================================
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        if (par1World.provider.dimensionId > 0 || par1World.getBlockId(par2, par3 - 1, par4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(par1World, par2, par3, par4)) {
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4)) {
                par1World.setBlockToAir(par2, par3, par4);
            }
            else {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par1World.rand.nextInt(10));
            }
        }
    }
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if(par5Random.nextInt(24) == 0) {
            par1World.playSound((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "fire.fire", 1.0F + par5Random.nextFloat(), par5Random.nextFloat() * 0.7F + 0.3F, false);
        }

        int l;
        float f;
        float f1;
        float f2;

        if(!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !Block.fire.canBlockCatchFire(par1World, par2, par3 - 1, par4, UP)) {
            if(Block.fire.canBlockCatchFire(par1World, par2 - 1, par3, par4, EAST)) {
                for(l = 0; l < 2; ++l) {
                    f = (float)par2 + par5Random.nextFloat() * 0.1F;
                    f1 = (float)par3 + par5Random.nextFloat();
                    f2 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if(Block.fire.canBlockCatchFire(par1World, par2 + 1, par3, par4, WEST)) {
                for (l = 0; l < 2; ++l) {
                    f = (float)(par2 + 1) - par5Random.nextFloat() * 0.1F;
                    f1 = (float)par3 + par5Random.nextFloat();
                    f2 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if(Block.fire.canBlockCatchFire(par1World, par2, par3, par4 - 1, SOUTH)) {
                for (l = 0; l < 2; ++l) {
                    f = (float)par2 + par5Random.nextFloat();
                    f1 = (float)par3 + par5Random.nextFloat();
                    f2 = (float)par4 + par5Random.nextFloat() * 0.1F;
                    par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if(Block.fire.canBlockCatchFire(par1World, par2, par3, par4 + 1, NORTH)) {
                for (l = 0; l < 2; ++l) {
                    f = (float)par2 + par5Random.nextFloat();
                    f1 = (float)par3 + par5Random.nextFloat();
                    f2 = (float)(par4 + 1) - par5Random.nextFloat() * 0.1F;
                    par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if(Block.fire.canBlockCatchFire(par1World, par2, par3 + 1, par4, DOWN)) {
                for (l = 0; l < 2; ++l) {
                    f = (float)par2 + par5Random.nextFloat();
                    f1 = (float)(par3 + 1) - par5Random.nextFloat() * 0.1F;
                    f2 = (float)par4 + par5Random.nextFloat();
                    par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else {
            for(l = 0; l < 3; ++l) {
                f = (float)par2 + par5Random.nextFloat();
                f1 = (float)par3 + par5Random.nextFloat() * 0.5F + 0.5F;
                f2 = (float)par4 + par5Random.nextFloat();
                par1World.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
    	AssetManager.addIconGroup(blockName, DemonMobs.domain, new String[] {"hellfire_layer_0", "hellfire_layer_1"}, iconRegister);
    }
    
    // ========== Get Specific Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getFireIcon(int par1) {
        return AssetManager.getIconGroup(blockName)[par1];
    }
    
    // ========== Get Icon from Side and Meta ==========
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int par1, int par2) {
        return AssetManager.getIconGroup(blockName)[0];
    }
}
