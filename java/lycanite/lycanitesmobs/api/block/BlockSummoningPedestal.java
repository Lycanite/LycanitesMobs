package lycanite.lycanitesmobs.api.block;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.tileentity.TileEntityBase;
import lycanite.lycanitesmobs.api.tileentity.TileEntitySummoningPedestal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSummoningPedestal extends BlockBase implements ITileEntityProvider {
    public enum EnumSummoningPedestal implements IStringSerializable {
        NONE("none"),
        CLIENT("client"),
        PLAYER("player");

        private String name;
        EnumSummoningPedestal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
    public static final PropertyEnum PROPERTY_OWNER = PropertyEnum.create("owner", EnumSummoningPedestal.class);

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSummoningPedestal(GroupInfo group) {
		super(Material.iron);
        this.setCreativeTab(LycanitesMobs.itemsTab);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(PROPERTY_OWNER, EnumSummoningPedestal.NONE));
		
		// Properties:
		this.group = group;
		this.blockName = "summoningpedestal";
		this.setup();
		
		// Stats:
		this.setHardness(5F);
        this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
		this.setStepSound(SoundType.METAL);

        // Tile Entity:
        this.isBlockContainer = true;
	}

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTY_OWNER);
    }


    // ==================================================
    //                     Block Events
    // ==================================================
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if(tileentity instanceof TileEntitySummoningPedestal) {
            TileEntitySummoningPedestal tileEntitySummoningPedestal = (TileEntitySummoningPedestal)tileentity;
            tileEntitySummoningPedestal.setOwner(placer);
            if(placer instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)placer;
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if(playerExt != null) {
                    tileEntitySummoningPedestal.setSummonSet(playerExt.getSelectedSummonSet());
                }
            }
        }
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
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventParam);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            if(playerIn != null && playerIn.worldObj != null) {
                playerIn.openGui(LycanitesMobs.instance, GuiHandler.GuiType.TILEENTITY.id, playerIn.worldObj, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }


    // ==================================================
    //                    Tile Entity
    // ==================================================
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntitySummoningPedestal();
    }
}
