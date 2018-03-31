package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class MixBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public MixBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}

	@Override
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}


	/** Called every time liquids mix to form a block. **/
	public void onMix(World world, IBlockState blockState, BlockPos mixPos) {
		// Check Block:
		if(!this.isTriggerBlock(blockState, world, mixPos, 0)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && world.rand.nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, null, mixPos.up(), this.getBlockLevel(blockState, world, mixPos), 0);
	}
}
