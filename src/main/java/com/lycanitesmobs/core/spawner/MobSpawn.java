package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawn {
	/** The MobInfo to base this Mob Spawn off of (using the MobInfo's SpawnInfo). **/
	public MobInfo mobInfo;

	/** The Spawn Weight to use, if not set the MobInfo's weight is used instead. **/
	public int weight = -1;

	/** The Spawn Chance to use, if not set the MobInfo's chance is used instead. **/
	public double chance = -1;

	/** Used for the block-based spawn triggers. How many blocks that must be within the Spawn Block Search Range. **/
	public int blockCost = -1;


	/** Constructor **/
	public MobSpawn(MobInfo mobInfo) {
		this.mobInfo = mobInfo;
	}


	/** Loads this Mob Spawn from the provided JSON data. **/
	public void readJSON(JsonObject json) {
		// TODO Read MobSpawn JSON.
	}


	/** Returns if this mob can spawn at the provided coordinate. **/
	public boolean canSpawn(World world, BlockPos spawnPos, int blockCount) {
		// Block Cost:
		int blockCost = this.mobInfo.spawnInfo.spawnBlockCost;
		if(this.blockCost > 0) {
			blockCost = this.blockCost;
		}
		if(blockCount < blockCost) {
			return false;
		}

		// Chance:
		double chance = this.mobInfo.spawnInfo.spawnChance;
		if(this.chance > 0) {
			chance = this.chance;
		}
		if(chance < 1 && world.rand.nextDouble() > chance) {
			return false;
		}

		return true;
	}
}
