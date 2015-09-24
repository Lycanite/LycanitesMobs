package lycanite.lycanitesmobs.api.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.tileentity.TileEntityBase;
import lycanite.lycanitesmobs.api.tileentity.TileEntitySummoningPedestal;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSummoningPedestal extends BlockBase implements ITileEntityProvider {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSummoningPedestal(GroupInfo group) {
		super(Material.iron);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = group;
		this.blockName = "summoningpedestal";
		this.setup();
		
		// Stats:
		this.setHardness(5F);
        this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
		this.setStepSound(this.soundTypeMetal);

        // Tile Entity:
        this.isBlockContainer = true;
	}


    // ==================================================
    //                     Block Events
    // ==================================================
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        if(tileentity instanceof TileEntitySummoningPedestal) {
            TileEntitySummoningPedestal tileEntitySummoningPedestal = (TileEntitySummoningPedestal)tileentity;
            tileEntitySummoningPedestal.setOwner(entity);
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if(playerExt != null) {
                    tileEntitySummoningPedestal.setSummonSet(playerExt.getSelectedSummonSet());
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity != null && tileEntity instanceof TileEntityBase)
            ((TileEntityBase)tileEntity).onRemove();
        super.breakBlock(world, x, y, z, block, metadata);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventArg) {
        super.onBlockEventReceived(world, x, y, z, eventID, eventArg);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventArg);
    }


    // ==================================================
    //                    Tile Entity
    // ==================================================
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntitySummoningPedestal();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        String textureName = this.blockName;
        AssetManager.addIcon(textureName, this.group, this.getTextureName(), iconRegister);
        AssetManager.addIcon(textureName + "_side", this.group, this.getTextureName() + "_side", iconRegister);
        AssetManager.addIcon(textureName + "_top", this.group, this.getTextureName() + "_top", iconRegister);

        textureName = this.blockName + "_player";
        AssetManager.addIcon(textureName, this.group, this.getTextureName(), iconRegister);
        AssetManager.addIcon(textureName + "_side", this.group, this.getTextureName() + "_player_side", iconRegister);
        AssetManager.addIcon(textureName + "_top", this.group, this.getTextureName() + "_player_top", iconRegister);

        textureName = this.blockName + "_client";
        AssetManager.addIcon(textureName, this.group, this.getTextureName(), iconRegister);
        AssetManager.addIcon(textureName + "_side", this.group, this.getTextureName() + "_client_side", iconRegister);
        AssetManager.addIcon(textureName + "_top", this.group, this.getTextureName() + "_client_top", iconRegister);
    }

    // ========== Get Icon from Side and Metadata ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int metadata) {
        String textureName = this.blockName + "_client";
        if(side == 0)
            return AssetManager.getIcon(textureName);
        if(side == 1)
            return AssetManager.getIcon(textureName + "_top");
        return AssetManager.getIcon(textureName + "_side");
    }

    // ========== Get Icon from Side and Metadata with Block Access ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        String textureName = this.blockName;
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity != null && tileEntity instanceof TileEntitySummoningPedestal) {
            TileEntitySummoningPedestal tileEntitySummoningPedestal = (TileEntitySummoningPedestal)tileEntity;
            if(tileEntitySummoningPedestal.getOwnerUUID() != null) {
                if(tileEntitySummoningPedestal.getOwnerUUID().equals(LycanitesMobs.proxy.getClientPlayer().getUniqueID()))
                    textureName += "_client";
                else
                    textureName += "_player";
            }
        }

        if(side == 0)
            return AssetManager.getIcon(textureName);
        if(side == 1)
            return AssetManager.getIcon(textureName + "_top");
        return AssetManager.getIcon(textureName + "_side");
    }
}
