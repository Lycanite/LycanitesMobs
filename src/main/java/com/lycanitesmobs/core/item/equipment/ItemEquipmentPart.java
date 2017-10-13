package com.lycanitesmobs.core.item.equipment;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;

import java.util.ArrayList;
import java.util.List;

public class ItemEquipmentPart extends ItemBase {
	/** A list of all features this part has. **/
	public List<EquipmentFeature> features = new ArrayList<>();

	/** The slot type that this part must fit into. Can be: base, head, blade, axe, pike or jewel. **/
	public String slotType;

	/** The id of the mob that drops this part. **/
	public String dropMobId;

	/** The default chance of the part being dropped by a mob. **/
	public double dropChance = 1;

	/** The minimum random level that this part can be. **/
	public int levelMin = 1;

	/** The maximum random level that this part can be. **/
	public int levelMax = 3;

	/** The level of this equipment part. TODO Must be loaded from the item data. **/
	public int level = 1;

	/** The dye of this equipment part. -1 = No dye. TODO Must be loaded from the item data. **/
	public int dye = -1;


	// ==================================================
	//                   Constructor
	// ==================================================
	public ItemEquipmentPart(GroupInfo groupInfo) {
		super();
		this.group = groupInfo;
		this.setMaxStackSize(1);
	}

	/** Loads this feature from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.itemName = json.get("itemName").getAsString();
		this.slotType = json.get("slotType").getAsString();

		if(json.has("dropMobId"))
			this.dropMobId = json.get("dropMobId").getAsString();

		if(json.has("dropChance"))
			this.dropChance = json.get("dropChance").getAsDouble();

		if(json.has("levelMin"))
			this.levelMin = json.get("levelMin").getAsInt();

		if(json.has("levelMax"))
			this.levelMax = json.get("levelMax").getAsInt();
	}
}
