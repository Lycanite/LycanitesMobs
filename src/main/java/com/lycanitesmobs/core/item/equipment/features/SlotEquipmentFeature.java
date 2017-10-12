package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;

public class SlotEquipmentFeature extends EquipmentFeature {
	/** The type of slot that this adds to the part. Can be: head, blade, axe, pike or jewel. Shouldn't be base else you can have infinitely large weapons! **/
	String slotType;

	/** Loads this slot from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.slotType = json.get("slotType").getAsString();
	}
}
