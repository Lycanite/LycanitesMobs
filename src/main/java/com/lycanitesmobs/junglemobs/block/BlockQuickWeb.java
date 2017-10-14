package com.lycanitesmobs.junglemobs.block;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.junglemobs.JungleMobs;
import com.lycanitesmobs.core.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockQuickWeb extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockQuickWeb() {
		super(Material.WEB);
        this.setCreativeTab(LycanitesMobs.blocksTab);
		
		// Properties:
		this.group = JungleMobs.instance.group;
		this.blockName = "quickweb";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Quickweb", true) ? 200 : 1;
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
    public Item getItemDropped(IBlockState blockState, Random random, int fortune) {
        return ObjectManager.getItem("quickwebcharge");
    }

    @Override
    public int damageDropped(IBlockState blockState) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);
        entity.setInWeb();
    }


    // ==================================================
    //                      Rendering
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
