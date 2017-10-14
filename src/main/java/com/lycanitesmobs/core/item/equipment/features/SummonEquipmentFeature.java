package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class SummonEquipmentFeature extends EquipmentFeature {
	/** The id of the mob to summon. **/
	public String summonMobId;

	/** The chance on hot of summoning mobs. **/
	public double summonChance = 0.05;

	/** How long in ticks the summoned creature lasts for. **/
	public int summonDuration = 60;

	/** The minimum amount of mobs to summon. **/
	public int summonCountMin = 1;

	/** The maximum amount of mobs to summon. **/
	public int summonCountMax = 1;


	@Override
	public void loadFromJSON(JsonObject json) {
		this.summonMobId = json.get("summonMobId").getAsString();

		if(json.has("summonChance"))
			this.summonChance = json.get("summonChance").getAsDouble();

		if(json.has("summonDuration"))
			this.summonDuration = json.get("summonDuration").getAsInt();

		if(json.has("summonCountMin"))
			this.summonCountMin = json.get("summonCountMin").getAsInt();

		if(json.has("summonCountMax"))
			this.summonCountMax = json.get("summonCountMax").getAsInt();

		super.loadFromJSON(json);
	}

	@Override
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String description = I18n.translateToLocal("equipment.feature." + this.featureType) + " " + I18n.translateToLocal("entity." + this.summonMobId + ".name");
		description += "\n" + I18n.translateToLocal("equipment.feature.summon.chance") + " " + Math.round(this.summonChance * 100) + "%";
		if(this.summonDuration > 0) {
			description += "\n" + I18n.translateToLocal("equipment.feature.effect.duration") + " " + ((float)this.summonDuration / 20);
		}
		if(this.summonCountMin != this.summonCountMax) {
			description += "\n" + I18n.translateToLocal("equipment.feature.summon.count") + " " + this.summonCountMin + " - " + this.summonCountMax;
		}
		else {
			description += "\n" + I18n.translateToLocal("equipment.feature.summon.count") + " " + this.summonCountMax;
		}
		return description;
	}
}
