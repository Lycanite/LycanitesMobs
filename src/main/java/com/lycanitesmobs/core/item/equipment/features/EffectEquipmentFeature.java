package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;

public class EffectEquipmentFeature extends EquipmentFeature {
	/** How much damage this part adds to the weapon attack. **/
	public String effectType;

	/** The time (in ticks) that this feature adds to the weapon attack cooldown on use. **/
	public int effectDuration = 0;

	/** The range (in blocks) that this feature adds to the weapon attack. **/
	public int effectStrength = 0;


	@Override
	public void loadFromJSON(JsonObject json) {
		this.effectType = json.get("effectType").getAsString();

		if(json.has("effectDuration"))
			this.effectDuration = json.get("effectDuration").getAsInt();

		if(json.has("effectStrength"))
			this.effectStrength = json.get("effectStrength").getAsInt();

		super.loadFromJSON(json);
	}
}
