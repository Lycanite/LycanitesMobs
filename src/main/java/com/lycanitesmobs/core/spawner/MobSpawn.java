package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.MobDrop;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class MobSpawn {
	/** The MobInfo to base this Mob Spawn off of (using the MobInfo's SpawnInfo for default values). **/
	public MobInfo mobInfo;

	/** If set to true, the Forge Can Spawn Event is fired but its result is ignored, use this to prevent other mods from stopping the spawn via the event. **/
	protected boolean ignoreForgeCanSpawnEvent = false;

	/** If set to true, all mob instance spawn checks are ignored. This includes all checks for none Lycanites Mobs, Group Limits and Light Levels. **/
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

	/** Sets a custom name tag to a mob spawned with this Mob Spawn. **/
	protected String mobNameTag = "";

	/** Whether the spawned mob will be persistent and wont naturally despawn. Can be: default (SpawnInfo), true or false. **/
	protected String naturalDespawn = "default";

	/** If set, the spawned mob will be set as temporary where it will forcefully despawn after the specified time (in ticks). Useful for Mob Events. **/
	protected int temporary = -1;

	/** A custom scale for the physical size of the spawned mob. Only works with Lycanites Mobs. **/
	protected double mobSizeScale = -1;

	/** If set, the spawned mob will have its subspecies changed to this value. Only works with Lycanites Mobs. **/
	protected int subspecies = -1;

	/** If true, the spawned mob will fixate on the player that triggered the spawn, always attacking that player. **/
	protected boolean fixate = false;

	/** The level boost of the mob spawned, higher levels increase the stats by a small amount. This is added to the starting mob level (normally just 1). **/
	protected int mobLevel = 0;

	/** A list of item drops to add to a mob spawned by this MobSpawn. **/
	protected List<MobDrop> mobDrops = new ArrayList<>();


	/** Loads this Spawn Condition from the provided JSON data. **/
	public static MobSpawn createFromJSON(JsonObject json) {
		MobSpawn mobSpawn = null;
		if(json.has("mobId")) {
			String mobId = json.get("mobId").getAsString();
			MobInfo mobInfo = MobInfo.getFromId(mobId);
			if(mobInfo != null) {
				mobSpawn = new MobSpawn(mobInfo);
				mobSpawn.loadFromJSON(json);
			}
			else {
				LycanitesMobs.printWarning("", "[JSONSpawner] Unable to find a Lycanites Mob from the mob id: " + mobId + " Mob Spawn entry ignored.");
			}
		}
		return mobSpawn;
	}


	/** Constructor **/
	public MobSpawn(MobInfo mobInfo) {
		this.mobInfo = mobInfo;
	}

	/** Loads this Mob Spawn from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("ignoreForgeCanSpawnEvent"))
			this.ignoreForgeCanSpawnEvent = json.get("ignoreForgeCanSpawnEvent").getAsBoolean();

		if(json.has("ignoreDimension"))
			this.ignoreDimension = json.get("ignoreDimension").getAsBoolean();

		if(json.has("biomeCheck"))
			this.biomeCheck = json.get("biomeCheck").getAsString();

		if(json.has("ignoreLightLevel"))
			this.ignoreLightLevel = json.get("ignoreLightLevel").getAsBoolean();

		if(json.has("ignoreGroupLimit"))
			this.ignoreGroupLimit = json.get("ignoreGroupLimit").getAsBoolean();

		if(json.has("ignoreForgeCanSpawnEvent"))
			this.ignoreForgeCanSpawnEvent = json.get("ignoreForgeCanSpawnEvent").getAsBoolean();

		if(json.has("weight"))
			this.weight = json.get("weight").getAsInt();

		if(json.has("chance"))
			this.chance = json.get("chance").getAsDouble();

		if(json.has("blockCost"))
			this.blockCost = json.get("blockCost").getAsInt();

		if(json.has("mobNameTag"))
			this.mobNameTag = json.get("mobNameTag").getAsString();

		if(json.has("naturalDespawn"))
			this.naturalDespawn = json.get("naturalDespawn").getAsString();

		if(json.has("temporary"))
			this.temporary = json.get("temporary").getAsInt();

		if(json.has("mobSizeScale"))
			this.mobSizeScale = json.get("mobSizeScale").getAsDouble();

		if(json.has("subspecies"))
			this.subspecies = json.get("subspecies").getAsInt();

		if(json.has("fixate"))
			this.fixate = json.get("fixate").getAsBoolean();

		if(json.has("mobLevel"))
			this.mobLevel = json.get("mobLevel").getAsInt();

		if(json.has("mobDrops")) {
			JsonArray mobDropEntries = json.getAsJsonArray("mobDrops");
			for(JsonElement mobDropJson : mobDropEntries) {
				MobDrop mobDrop = MobDrop.createFromJSON(mobDropJson.getAsJsonObject());
				if(mobDrop != null) {
					this.mobDrops.add(mobDrop);
				}
			}
		}
	}


	/**
	 * Returns if this mob can spawn at the provided coordinate. This is a light check and does not perform an environmental check.
	 * @param world The world to spawn in.
	 * @param blockCount The number of spawn blocks found.
	 * @param biomes A list of biomes to check, if null, the biome check is ignored.
	 * @param forceIgnoreDimension If true, the dimension check is ignored.
	 **/
	public boolean canSpawn(World world, int blockCount, List<Biome> biomes, boolean forceIgnoreDimension) {
		// Enabled:
		if(this.mobInfo != null && !this.mobInfo.mobEnabled) {
			return false;
		}

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

		// Dimension:
		if(!forceIgnoreDimension && !this.ignoreDimension && !this.mobInfo.spawnInfo.isAllowedDimension(world)) {
			return false;
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
	 * Gets if the spawned mob should be forced to not despawn, either the overridden value or the default SpawnInfo value.
	 **/
	public boolean getNaturalDespawn() {
		if("true".equalsIgnoreCase(this.naturalDespawn)) {
			return true;
		}
		if("false".equalsIgnoreCase(this.naturalDespawn)) {
			return false;
		}
		return this.mobInfo.spawnInfo.despawnNatural;
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


	/**
	 * Called when a mob is spawned from this MobSpawn.
	 **/
	public void onSpawned(EntityLiving entityLiving, EntityPlayer player) {
		if(!"".equals(this.mobNameTag)) {
			entityLiving.setCustomNameTag(this.mobNameTag);
		}
		if(!this.getNaturalDespawn()) {
			entityLiving.enablePersistence();
		}

		if(entityLiving instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entityLiving;
			if(this.mobSizeScale > -1) {
				entityCreature.setSizeScale(this.mobSizeScale);
			}
			if(this.subspecies > -1) {
				entityCreature.setSubspecies(this.subspecies, true);
			}
			if(this.fixate && player != null) {
				entityCreature.setFixateTarget(player);
			}
			if(this.mobLevel > 0) {
				entityCreature.addLevel(this.mobLevel);
			}
			if(this.temporary > -1) {
				entityCreature.setTemporary(this.temporary);
			}
			for(MobDrop mobDrop : this.mobDrops) {
				entityCreature.drops.add(mobDrop);
			}
		}
	}
}
