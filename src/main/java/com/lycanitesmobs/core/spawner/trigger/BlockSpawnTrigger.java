package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering when certain blocks are broken by the player. **/

	/** The Chance of triggering. **/
	public double chance = 1;

	/** Whether fake players (such as BuildCraft quarries) should trigger this also. **/
	public boolean ignoreFakePlayers = true;

	// TODO Add various block definitions.


	/** Constructor **/
	public BlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	/** Called every time a player breaks a block. **/
	public void onTick(EntityPlayer player, BlockPos breakPos, IBlockState blockState) {
		// Check Player:
		if(this.ignoreFakePlayers && player instanceof FakePlayer) {
			return;
		}

		// Check Block:
		if(!this.isTriggerBlock(blockState, player.getEntityWorld(), breakPos)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition(), this.getBlockLevel(blockState, player.getEntityWorld(), breakPos));
	}

	/** Returns true if the provided block is a match for this trigger. **/
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos) {
		return true;
	}

	/** Returns a value to represent the block's rarity for higher level spawns with increased chances of tougher mobs, etc. **/
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}
}
