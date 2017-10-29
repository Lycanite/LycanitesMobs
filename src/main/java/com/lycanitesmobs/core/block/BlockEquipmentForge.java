package com.lycanitesmobs.core.block;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.tileentity.TileEntityBase;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEquipmentForge extends BlockBase implements ITileEntityProvider {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public int level;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockEquipmentForge(GroupInfo group, int level) {
		super(level <= 1 ? Material.WOOD : level == 2 ? Material.ROCK : Material.IRON);
        this.setCreativeTab(LycanitesMobs.blocksTab);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		
		// Properties:
		this.group = group;
		this.level = level;
		if(level <= 1) {
			this.blockName = "equipmentforge_wood";
			this.setHarvestLevel("axe", 1);
			this.setSoundType(SoundType.WOOD);
			this.setResistance(10F);
		}
		else if(level == 2) {
			this.blockName = "equipmentforge_stone";
			this.setHarvestLevel("pickaxe", 1);
			this.setSoundType(SoundType.STONE);
			this.setResistance(20F);
		}
		else {
			this.blockName = "equipmentforge_iron";
			this.setHarvestLevel("pickaxe", 2);
			this.setSoundType(SoundType.METAL);
			this.setResistance(1000F);
		}
		this.setup();
		
		// Stats:
		this.setHardness(5F);

        // Tile Entity:
        this.isBlockContainer = true;
	}

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }


    // ==================================================
    //                     Block Events
    // ==================================================
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		this.setDefaultFacing(world, pos, state);
        super.onBlockAdded(world, pos, state);
    }

	protected void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			IBlockState iblockstate = world.getBlockState(pos.north());
			IBlockState iblockstate1 = world.getBlockState(pos.south());
			IBlockState iblockstate2 = world.getBlockState(pos.west());
			IBlockState iblockstate3 = world.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			}
			else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			}
			else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			}
			else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity != null && tileEntity instanceof TileEntityBase)
            ((TileEntityBase)tileEntity).onRemove();
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventParam);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            if(playerIn != null && playerIn.getEntityWorld() != null) {
                playerIn.openGui(LycanitesMobs.instance, GuiHandler.GuiType.TILEENTITY.id, playerIn.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }


    // ==================================================
    //                    Tile Entity
    // ==================================================
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
		TileEntityEquipmentForge tileEntityEquipmentForge = new TileEntityEquipmentForge();
		tileEntityEquipmentForge.setLevel(this.level);
        return tileEntityEquipmentForge;
    }


    // ==================================================
    //                    Block State
    // ==================================================
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

    @Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
}
