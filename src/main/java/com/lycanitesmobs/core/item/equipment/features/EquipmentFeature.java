package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;

public class EquipmentFeature {
	/** The minimun level the part must be for this feature to be enabled. **/
	int levelMin = -1;

	/** The maximun level the part must be for this feature to be enabled. **/
	int levelMax = -1;


	/** Loads a Tool Feature from the provided JSON data. **/
	public static EquipmentFeature createFromJSON(JsonObject json) {
		String type = json.get("featureType").getAsString();
		EquipmentFeature equipmentFeature = null;

		if("slot".equalsIgnoreCase(type)) {
			equipmentFeature = new SlotEquipmentFeature();
		}
		else if("harvest".equalsIgnoreCase(type)) {
			equipmentFeature = new HarvestEquipmentFeature();
		}
		else if("damage".equalsIgnoreCase(type)) {
			equipmentFeature = new DamageEquipmentFeature();
		}
		else if("effect".equalsIgnoreCase(type)) {
			equipmentFeature = new EffectEquipmentFeature();
		}
		else if("summon".equalsIgnoreCase(type)) {
			equipmentFeature = new SummonEquipmentFeature();
		}

		equipmentFeature.loadFromJSON(json);
		return equipmentFeature;
	}


	/** Loads this feature from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("levelMin"))
			this.levelMin = json.get("levelMin").getAsInt();

		if(json.has("levelMax"))
			this.levelMax = json.get("levelMax").getAsInt();
	}


	/** Returns true if this feature is active for the provided part. **/
	public boolean isActive(ItemEquipmentPart equipmentPart) {
		// Check Level:
		if(this.levelMin > -1 && equipmentPart.level < this.levelMin) {
			return false;
		}
		if(this.levelMax > -1 && equipmentPart.level > this.levelMax) {
			return false;
		}

		return true;
	}
}
