package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class BlockSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering when certain blocks are broken by the player. **/

	/** The Chance of triggering. **/
	public double chance = 1;

	// TODO Add various block definitions.


	/** Constructor **/
	public BlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	/** Called every time a player breaks a block. **/
	public void onTick(EntityPlayer player, BlockPos breakPos, IBlockState blockState) {
		// Check Block:
		// TODO Check if the broken block matches this trigger.

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition());
	}
}
