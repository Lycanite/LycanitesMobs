package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.SpawnerJSONUtilities;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MaterialSpawnLocation extends BlockSpawnLocation {
    /** A list of block materials to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Material> materials = new ArrayList<>();


	@Override
	public void loadFromJSON(JsonObject json) {
		this.materials = SpawnerJSONUtilities.getJsonMaterials(json);

		super.loadFromJSON(json);
	}

	/** Returns if the provided block position is valid. **/
	@Override
	public boolean isValidBlock(World world, BlockPos blockPos) {
		IBlockState blockState = world.getBlockState(blockPos);
		if(blockState == null) {
			return false;
		}

		if(!this.surface || !this.underground) {
			if(world.canSeeSky(blockPos)) {
				if(!this.surface) {
					return false;
				}
			}
			else {
				if(!this.underground) {
					return false;
				}
			}
		}

		if("blacklist".equalsIgnoreCase(this.listType)) {
			return !this.materials.contains(blockState);
		}
		else {
			return this.materials.contains(blockState);
		}
	}
}
