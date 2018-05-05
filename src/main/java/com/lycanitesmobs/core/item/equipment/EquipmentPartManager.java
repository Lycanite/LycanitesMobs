package com.lycanitesmobs.core.item.equipment;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.info.GroupInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentPartManager extends JSONLoader {

	public static EquipmentPartManager INSTANCE;

	public Map<String, ItemEquipmentPart> equipmentParts = new HashMap<>();

	/** A list of mod groups that have loaded with this Equipment Part Manager. **/
	public List<GroupInfo> loadedGroups = new ArrayList<>();


	/** Returns the main EquipmentPartManager INSTANCE or creates it and returns it. **/
	public static EquipmentPartManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EquipmentPartManager();
		}
		return INSTANCE;
	}


	/** Loads all JSON Equipment Parts. Should only be done on pre-init. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
		if(!this.loadedGroups.contains(groupInfo)) {
			this.loadedGroups.add(groupInfo);
		}
		try {
			this.loadAllJson(groupInfo, "Equipment", "equipment", "itemName", false);
			LycanitesMobs.printDebug("Equipment", "Complete! " + this.equipmentParts.size() + " JSON Equipment Parts Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "No Equipment loaded for: " + groupInfo.name);
		}
	}


	@Override
	public void parseJson(GroupInfo groupInfo, String name, JsonObject json) {
		ItemEquipmentPart equipmentPart = new ItemEquipmentPart(groupInfo);
		equipmentPart.loadFromJSON(json);
		if(this.equipmentParts.containsKey(equipmentPart.itemName)) {
			LycanitesMobs.printWarning("", "[Equipment] Tried to add a Equipment Part with a name that is already in use: " + equipmentPart.itemName);
			return;
		}
		if(this.equipmentParts.values().contains(equipmentPart)) {
			LycanitesMobs.printWarning("", "[Equipment] Tried to add a Equipment Part that is already added: " + equipmentPart.itemName);
			return;
		}
		this.equipmentParts.put(equipmentPart.itemName, equipmentPart);
		ObjectManager.addItem(equipmentPart.itemName, equipmentPart);
	}


	/**
	 * Reloads all Equipment part JSON.
	 */
	public void reload() {
		this.equipmentParts.clear();
		for(GroupInfo group : this.loadedGroups) {
			this.loadAllFromJSON(group);
		}
	}
}
