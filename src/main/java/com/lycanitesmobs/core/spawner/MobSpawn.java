package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class MobSpawn {
	/** The MobInfo to base this Mob Spawn off of (using the MobInfo's SpawnInfo). **/
	public MobInfo mobInfo;

	/** If set to true, the Forge Can Spawn Event is fired but its result is ignored, use this to prevent other mods from stopping the spawn via the event. **/
	protected boolean ignoreForgeCanSpawnEvent = false;

	/** If set to true, all mob instance spawn checks are ignored. This includes all checks for none Lycnaites Mobs, Group Limits and Light Levels. **/
	protected boolean ignoreMobInstanceConditions = false;

	/** If set to true, this mob will ignore Dimension checks. This will not prevent a World Spawn Condition Dimension check however. **/
	protected boolean ignoreDimension = false;

	/** Whether this MobSpawn will ignore Biome checks. Can be ignore, check or default (use SpawnInfo). **/
	protected String biomeCheck = "default";

	/** If set to true, this mob will ignore Light Level checks. **/
	protected boolean ignoreLightLevel = false;

	/** If set to true, this mob will ignore Group Limit checks. **/
	protected boolean ignoreGroupLimit = false;

	/** The Spawn Weight to use, if not set the MobInfo's weight is used instead. **/
	protected int weight = -1;

	/** The Spawn Chance to use, if not set the MobInfo's chance is used instead. **/
	protected double chance = -1;

	/** Used for the block-based spawn triggers. How many blocks that must be within the Spawn Block Search Range. **/
	protected int blockCost = -1;


	/** Loads this Spawn Condition from the provided JSON data. **/
	public static MobSpawn createFromJSON(JsonObject json) {
		MobSpawn mobSpawn = null;
		mobSpawn.loadFromJSON(json);
		return mobSpawn;
	}


	/** Constructor **/
	public MobSpawn(MobInfo mobInfo) {
		this.mobInfo = mobInfo;
	}

	/** Loads this Mob Spawn from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		// TODO Read MobSpawn JSON.
	}


	/**
	 * Returns if this mob can spawn at the provided coordinate. This is a light check and does not perform an environmental check.
	 * @param world The world to spawn in.
	 * @param blockCount The number of spawn blocks found.
	 * @param biomes A list of biomes to check, if null, the biome check is ignored.
	 **/
	public boolean canSpawn(World world, int blockCount, List<Biome> biomes) {
		// Peaceful Difficulty:
		if(world.getDifficulty() == EnumDifficulty.PEACEFUL && !this.mobInfo.peacefulDifficulty) {
			return false;
		}

		// Weight:
		if(this.getWeight() <= 0) {
			return false;
		}

		// Block Count:
		if(blockCount < this.getBlockCost()) {
			return false;
		}

		// Minimum World Day:
		if(this.mobInfo.spawnInfo.spawnMinDay > 0) {
			ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
			if(worldExt != null) {
				int day = (int) Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000D);
				if(day < this.mobInfo.spawnInfo.spawnMinDay) {
					return false;
				}
			}
		}

		// Biome:
		if(biomes != null && this.shouldCheckBiome()) {
			boolean biomeMatched = false;
			for(Biome validBiome : this.mobInfo.spawnInfo.biomes) {
				if(biomes.contains(validBiome)) {
					biomeMatched = true;
				}
			}
			if(!biomeMatched) {
				return false;
			}
		}

		// Chance:
		if(this.getChance() < 1 && world.rand.nextDouble() > this.getChance()) {
			return false;
		}

		return true;
	}


	/**
	 * Gets the Spawn Block Cost to use, either the overridden MobSpawn Spawn Block Cost or the default SpawnInfo Spawn Block Cost.
	 **/
	public int getBlockCost() {
		if(this.blockCost > -1) {
			return this.blockCost;
		}
		return this.mobInfo.spawnInfo.spawnBlockCost;
	}


	/**
	 * Gets the Spawn Chance to use, either the overridden MobSpawn Spawn Chance or the default SpawnInfo Spawn Chance.
	 **/
	public double getChance() {
		if(this.chance > -1) {
			return this.chance;
		}
		return this.mobInfo.spawnInfo.spawnChance;
	}


	/**
	 * Gets the Spawn Weight to use, either the overridden MobSpawn Spawn Weight or the default SpawnInfo Spawn Weight.
	 **/
	public int getWeight() {
		if(this.weight > -1) {
			return this.weight;
		}
		return this.mobInfo.spawnInfo.spawnWeight;
	}


	/**
	 * Returns true if this Mob Spawn should check biomes and false if it should ignore them.
	 **/
	public boolean shouldCheckBiome() {
		if("ignore".equalsIgnoreCase(this.biomeCheck)) {
			return false;
		}
		if("check".equalsIgnoreCase(this.biomeCheck)) {
			return true;
		}
		return !this.mobInfo.spawnInfo.ignoreBiome;
	}
}
