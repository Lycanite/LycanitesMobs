package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.config.ConfigSpawning.SpawnDimensionSet;
import com.lycanitesmobs.core.config.ConfigSpawning.SpawnTypeSet;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CreatureSpawnConfig {
	public double spawnLimitRange = 32D;
	public boolean disableAllSpawning = false;
	public boolean disableDungeonSpawners = false;
	public boolean enforceBlockCost = true;
	public boolean useSurfaceLightLevel = true;
	public double spawnWeightScale = 1.0D;
	public double dungeonSpawnerWeightScale = 1.0D;
	public boolean ignoreWorldGenSpawning = false;
	public boolean controlVanillaSpawns = true;

	/** A global list of dimension ids that overrides every other spawn setting in both the configs and json spawners. **/
	public int[] dimensionList;

	/** If set to true the dimension list acts as a whitelist, otherwise it is a blacklist. **/
	public boolean dimensionListWhitelist = false;
	

	/**
	 * Loads global spawning settings from the configs.
	 */
	public void loadConfig(ConfigBase config) {
		config.setCategoryComment("Global Spawning", "These settings are used by everything. It is recommended to leave them as they are however low end machines might benefit from a few tweaks here.");
        this.spawnLimitRange = config.getDouble("Global Spawning", "Mob Limit Search Range", this.spawnLimitRange, "When spawned form a vanilla spawner, this is how far a mob should search from in blocks when checking how many of its kind have already spawned. Custom Spawners have it defined in their json file instead.");
		this.disableAllSpawning = config.getBool("Global Spawning", "Disable Spawning", this.disableAllSpawning, "If true, all mobs from this mod will not spawn at all.");
		this.enforceBlockCost = config.getBool("Global Spawning", "Enforce Block Costs", this.enforceBlockCost, "If true, mobs will double check if their required blocks are nearby, such as Cinders needing so many blocks of fire.");
		this.spawnWeightScale = config.getDouble("Global Spawning", "Weight Scale", this.spawnWeightScale, "Scales the spawn weights of all mobs from this mod. For example, you can use this to quickly half the spawn rates of mobs from this mod compared to vanilla/other mod mobs by setting it to 0.5.");
		this.useSurfaceLightLevel = config.getBool("Global Spawning", "Use Surface Light Level", this.useSurfaceLightLevel, "If true, when water mobs spawn, instead of checking the light level of the block the mob is spawning at, the light level of the surface (if possible) is checked. This stops mobs like Jengus from spawning at the bottom of deep rivers during the day, set to false for the old way.");
		this.ignoreWorldGenSpawning = config.getBool("Global Spawning", "Ignore WorldGen Spawning", this.ignoreWorldGenSpawning, "If true, when new world chunks are generated, no mobs from this mod will pre-spawn (mobs will still attempt to spawn randomly afterwards). Set this to true if you are removing mobs from vanilla dimensions as the vanilla WorldGen spawning ignores mob spawn conditions.");
		this.controlVanillaSpawns = config.getBool("Global Spawning", "Edit Vanilla Spawning", this.controlVanillaSpawns, "If true, some vanilla spawns in various biomes will be removed, note that vanilla mobs should still be easy to find, only they will be more biome specific.");

		// Master Dimension List:
		String dimensionListValue = config.getString("Global Spawning", "Master Spawn Dimensions", "", "A global comma separated list of dimension ids that overrides every other spawn setting in both the configs and json spawners. Use this to quickly stop all mobs from spawning in certain dimensions, etc.");
		List<Integer> dimensionEntries = new ArrayList<>();
		for(String dimensionEntry : dimensionListValue.replace(" ", "").split(",")) {
			if(NumberUtils.isCreatable(dimensionEntry)) {
				dimensionEntries.add(Integer.parseInt(dimensionEntry));
			}
		}
		this.dimensionList = ArrayUtils.toPrimitive(dimensionEntries.toArray(new Integer[dimensionEntries.size()]));
		this.dimensionListWhitelist = config.getBool("Global Spawning", "Master Spawn Dimensions Whitelist", this.dimensionListWhitelist, "If set to true the dimension list acts as a whitelist, otherwise it is a blacklist.");

		config.setCategoryComment("Dungeon Features", "Here you can set special features used in dungeon generation.");
		this.disableDungeonSpawners = config.getBool("Dungeon Features", "Disable Dungeon Spawners", this.disableDungeonSpawners, "If true, newly generated dungeons wont create spawners with mobs from this mod.");
		this.dungeonSpawnerWeightScale = config.getDouble("Dungeon Features", "Dungeon Spawner Weight Scale", this.dungeonSpawnerWeightScale, "Scales the weight of dungeons using spawners from this mod. For example, you can half the chances all dungeons having spawners with mobs from this mod in them by setting this to 0.5.");
	}
}
