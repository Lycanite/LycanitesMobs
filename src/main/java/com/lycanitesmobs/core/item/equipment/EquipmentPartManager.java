package com.lycanitesmobs.core.item.equipment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.info.GroupInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EquipmentPartManager extends JSONLoader {

	public static EquipmentPartManager INSTANCE;

	public Map<String, ItemEquipmentPart> equipmentParts = new HashMap<>();


	/** Returns the main EquipmentPartManager INSTANCE or creates it and returns it. **/
	public static EquipmentPartManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EquipmentPartManager();
		}
		return INSTANCE;
	}

	/** Loads all JSON Equipment Parts. Should only be done on pre-init. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
		LycanitesMobs.printDebug("Equipment", "Loading JSON Equipment Parts!");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> equipmentPartJSONs = new HashMap<>();

		// Load Default Parts:
		Path path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.filename, "equipment");
		Map<String, JsonObject> defaultEquipmentPartJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultEquipmentPartJSONs, "itemName", null);

		// Load Custom Parts:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customDir = new File(configPath + "equipment");
		customDir.mkdirs();
		path = customDir.toPath();
		Map<String, JsonObject> customEquipmentPartJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customEquipmentPartJSONs, "itemName", null);


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultEquipmentPartJSONs, customEquipmentPartJSONs, equipmentPartJSONs, false, "equipment");


		// Create Equipment Parts:
		LycanitesMobs.printDebug("Equipment", "Loading " + equipmentPartJSONs.size() + " Equipment Parts...");
		for(String spawnerJSONName : equipmentPartJSONs.keySet()) {
			try {
				JsonObject spawnerJSON = equipmentPartJSONs.get(spawnerJSONName);
				LycanitesMobs.printDebug("Equipment", "Loading Equipment Part JSON: " + spawnerJSON);
				ItemEquipmentPart itemEquipmentPart = new ItemEquipmentPart(groupInfo);
				itemEquipmentPart.loadFromJSON(spawnerJSON);
				this.addEquipmentPart(itemEquipmentPart);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Equipment Part: " + spawnerJSONName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Equipment Part: " + spawnerJSONName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("Equipment", "Complete! " + this.equipmentParts.size() + " JSON Equipment Parts Loaded In Total.");
	}


	/** Adds a new Equipment Part to this Manager. Should only be done on pre-init. **/
	public void addEquipmentPart(ItemEquipmentPart equipmentPart) {
		if(this.equipmentParts.containsKey(equipmentPart.itemName)) {
			LycanitesMobs.printWarning("", "[Equipment Manager] Tried to add a Equipment Part with a name that is already in use: " + equipmentPart.itemName);
			return;
		}
		if(this.equipmentParts.values().contains(equipmentPart)) {
			LycanitesMobs.printWarning("", "[Equipment Manager] Tried to add a Equipment Part that is already added: " + equipmentPart.itemName);
			return;
		}
		this.equipmentParts.put(equipmentPart.itemName, equipmentPart);
		ObjectManager.addItem(equipmentPart.itemName, equipmentPart);
	}
}
