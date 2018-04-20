package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldSpawnCondition extends SpawnCondition {

	/** The dimension IDs that the world must or must not match depending on the list type. **/
	public int[] dimensionIds;

	/** How the dimension ID list works. Can be whitelist or blacklist. **/
	public String dimensionListType = "whitelist";

	/** How the biomes from the biome tags list works. Can be whitelist or blacklist. **/
	public String biomeTagListType = "whitelist";

	/** The list of biome tags to filter this condition by. **/
	public List<String> biomeTags = new ArrayList<>();

	/** The list of biomes generated from the list of biome tags. **/
	public List<Biome> biomesFromTags = null;

	/** How the biomes from the biome ids list works. Can be whitelist or blacklist. **/
	public String biomeIdListType = "whitelist";

	/** The list of specific biomes that this creature spawns in. **/
	public List<Biome> biomes = new ArrayList<>();

    /** The minimum world days that must have gone by, can accept fractions such as 5.25 for 5 and a quarter days. **/
    public double worldDayMin = -1;

    /** The maximum world days that this condition is true up to. **/
    public double worldDayMax = -1;

	/** The interval of days this condition is true such as every 7 days. **/
	public double worldDayN = -1;

    /** The minimum time of the current world day. **/
    public int dayTimeMin = -1;

	/** The maximum time of the current world day. **/
	public int dayTimeMax = -1;

    /** The weather, can be: any, clear, rain, storm, rainstorm (raining and thundering) or notclear (raining or thundering). **/
    public String weather = "any";

    /** The minimum difficulty level. **/
    public short difficultyMin = -1;

	/** The maximum difficulty level. **/
	public short difficultyMax = -1;

	/** The required moon phase. 0 is a full moon. **/
	public int moonPhase = -1;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("dimensionIds")) {
			JsonArray jsonArray = json.get("dimensionIds").getAsJsonArray();
			this.dimensionIds = new int[jsonArray.size()];
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			int i = 0;
			while (jsonIterator.hasNext()) {
				this.dimensionIds[i] = jsonIterator.next().getAsInt();
				i++;
			}
		}

		if(json.has("dimensionListType"))
			this.dimensionListType = json.get("dimensionListType").getAsString();

		if(json.has("biomes")) {
			this.biomeTags.clear();
			this.biomesFromTags = null;
			this.biomeTags = JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray());
		}

		if(json.has("biomeTagListType"))
			this.biomeTagListType = json.get("biomeTagListType").getAsString();

		this.biomes = JSONHelper.getJsonBiomes(json);

		if(json.has("biomeIdListType"))
			this.biomeIdListType = json.get("biomeIdListType").getAsString();

		if(json.has("worldDayMin"))
			this.worldDayMin = json.get("worldDayMin").getAsInt();

		if(json.has("worldDayMax"))
			this.worldDayMax = json.get("worldDayMax").getAsInt();

		if(json.has("worldDayN"))
			this.worldDayN = json.get("worldDayN").getAsInt();

		if(json.has("dayTimeMin"))
			this.dayTimeMin = json.get("dayTimeMin").getAsInt();

		if(json.has("dayTimeMax"))
			this.dayTimeMax = json.get("dayTimeMax").getAsInt();

		if(json.has("weather"))
			this.weather = json.get("weather").getAsString();

		if(json.has("difficultyMin"))
			this.difficultyMin = json.get("difficultyMin").getAsShort();

		if(json.has("difficultyMax"))
			this.difficultyMax = json.get("difficultyMax").getAsShort();

		if(json.has("moonPhase"))
			this.moonPhase = json.get("moonPhase").getAsInt();

		super.loadFromJSON(json);
	}


    @Override
    public boolean isMet(World world, EntityPlayer player, BlockPos position) {
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		int time = (int)Math.floor(world.getWorldTime() % 24000D);
		int day = (int)Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 23999D);

		// Check Dimension:
		if(this.dimensionIds != null) {
			boolean dimensionIdFound = false;
			for(int dimensionId : this.dimensionIds) {
				if(world.provider.getDimension() == dimensionId) {
					dimensionIdFound = true;
					break;
				}
			}
			if("whitelist".equalsIgnoreCase(this.dimensionListType) && !dimensionIdFound) {
				return false;
			}
			if("blacklist".equalsIgnoreCase(this.dimensionListType) && dimensionIdFound) {
				return false;
			}
		}

		// Check Day:
		if(this.worldDayMin >= 0 && day < this.worldDayMin) {
			return false;
		}
		if(this.worldDayMax >= 0 && day > this.worldDayMax) {
			return false;
		}
		if(this.worldDayN >= 0 && (day == 0 || day % this.worldDayN != 0)) {
			return false;
		}

		// Check Time:
		if(this.dayTimeMin >= 0 && time < this.dayTimeMin) {
			return false;
		}
		if(this.dayTimeMax >= 0 && time > this.dayTimeMax) {
			return false;
		}

		// Check Weather:
		if("clear".equalsIgnoreCase(this.weather) && (world.isRaining() || world.isThundering())) {
			return false;
		}
		else if("rain".equalsIgnoreCase(this.weather) && (!world.isRaining() || world.isThundering())) {
			return false;
		}
		else if("storm".equalsIgnoreCase(this.weather) && !world.isThundering()) {
			return false;
		}
		else if("rainstorm".equalsIgnoreCase(this.weather) && (!world.isRaining() || !world.isThundering())) {
			return false;
		}
		else if("notclear".equalsIgnoreCase(this.weather) && (!world.isRaining() && !world.isThundering())) {
			return false;
		}

		// Check Difficulty:
		if(this.difficultyMin >= 0 && world.getDifficulty().getDifficultyId() < this.difficultyMin) {
			return false;
		}
		if(this.difficultyMax >= 0 && world.getDifficulty().getDifficultyId() > this.difficultyMax) {
			return false;
		}

		// Check Moon Phase:
		if(this.moonPhase >= 0 && world.provider.getMoonPhase(world.getWorldTime()) != this.moonPhase) {
			return false;
		}

		// Check Biomes:
		if(!this.isAllowedBiome(world, position)) {
			return false;
		}

        return super.isMet(world, player, position);
    }


	/**
	 * Returns if the biome of the provided position passes this condition.
	 * @param world The world to get the biome from.
	 * @param position The position to get the biome from, can be null.
	 * @return True if the biome is allowed, false if not.
	 */
	public boolean isAllowedBiome(World world, BlockPos position) {
		if(position != null) {
			Biome biome = world.getBiome(position);

			// Biome IDs:
			if (!this.biomes.isEmpty()) {
				if (this.biomes.contains(biome)) {
					return !"blacklist".equalsIgnoreCase(this.biomeIdListType);
				}
			}

			// Biome Tags:
			if (!this.biomeTags.isEmpty()) {
				if (this.biomesFromTags == null) {
					this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
				}
				if (this.biomesFromTags.contains(biome)) {
					return !"blacklist".equalsIgnoreCase(this.biomeTagListType);
				}
			}
		}

		if(this.biomes.isEmpty() && this.biomeTags.isEmpty()) {
			return true;
		}

		return "blacklist".equalsIgnoreCase(this.biomeIdListType) && "blacklist".equalsIgnoreCase(this.biomeTagListType);
	}
}
