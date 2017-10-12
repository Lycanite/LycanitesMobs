package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;

public class SummonEquipmentFeature extends EquipmentFeature {
	/** The id of the mob to summon. **/
	public String summonMobId;

	/** The chance on hot of summoning mobs. **/
	public double summonChance = 0.05;

	/** The minimum amount of mobs to summon. **/
	public int summonCountMin = 1;

	/** The maximum amount of mobs to summon. **/
	public int summonCountMax = 1;


	@Override
	public void loadFromJSON(JsonObject json) {
		this.summonMobId = json.get("summonMobId").getAsString();

		if(json.has("summonChance"))
			this.summonChance = json.get("summonChance").getAsDouble();

		if(json.has("summonCountMin"))
			this.summonCountMin = json.get("summonCountMin").getAsInt();

		if(json.has("summonCountMax"))
			this.summonCountMax = json.get("summonCountMax").getAsInt();

		super.loadFromJSON(json);
	}
}
