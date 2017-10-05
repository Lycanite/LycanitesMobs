package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MaterialSpawnLocation extends BlockSpawnLocation {
    /** A list of block materials to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Material> materials = new ArrayList<>();


	@Override
	public void fromJSON(JsonObject json) {
		super.fromJSON(json);
	}

	/** Returns if the provided block position is valid. **/
	public boolean isValidBLock(World world, EntityPlayer player, BlockPos blockPos) {
		IBlockState blockState = world.getBlockState(blockPos);
		if(blockState == null) {
			return false;
		}

		if(this.surfaceOnly) {
			world.isAirBlock(blockPos.up());
		}

		if("blacklist".equalsIgnoreCase(this.listType)) {
			return !this.materials.contains(blockState);
		}
		else {
			return this.materials.contains(blockState);
		}
	}
}
