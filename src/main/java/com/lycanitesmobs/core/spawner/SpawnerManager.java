package com.lycanitesmobs.core.spawner;

import com.google.gson.*;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SpawnerManager extends JSONLoader {
	/** This manages all Spawners, it load them and can also destroy them. Spawners are then ran by Spawn Triggers which are called from the SpawnerEventListener. **/

	public static SpawnerManager INSTANCE;

	public Map<String, Spawner> spawners = new HashMap<>();
	public List<SpawnCondition> globalSpawnConditions = new ArrayList<>();


	/** Returns the main SpawnerManager INSTANCE or creates it and returns it. **/
	public static SpawnerManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SpawnerManager();
		}
		return INSTANCE;
	}


	/** Loads all JSON Spawners. **/
	public void loadAllFromJSON() {
		LycanitesMobs.printDebug("JSONSpawner", "Loading JSON Spawners!");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> spawnerJSONs = new HashMap<>();

		// Load Default Spawners:
		Path path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "spawners");
		Map<String, JsonObject> defaultSpawnerJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultSpawnerJSONs, "name", "spawner");

		// Load Default Mob Event Spawners:
		path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "mobevents");
		Map<String, JsonObject> defaultMobEventsJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultMobEventsJSONs, "name", "spawner");

		// Custom:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";

		// Load Custom Spawners:
		File customSpawnersDir = new File(configPath + "spawners");
		customSpawnersDir.mkdirs();
		path = customSpawnersDir.toPath();
		Map<String, JsonObject> customSpawnerJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customSpawnerJSONs, "name", "spawner");

		File customMobEventsDir = new File(configPath + "mobevents");
		path = customMobEventsDir.toPath();
		Map<String, JsonObject> customMobEventsJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customMobEventsJSONs, "name", "spawner");


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultSpawnerJSONs, customSpawnerJSONs, spawnerJSONs, true, "spawners");
		this.writeDefaultJSONObjects(gson, defaultMobEventsJSONs, customMobEventsJSONs, spawnerJSONs, true, "mobevents");


		// Create Spawners:
		LycanitesMobs.printDebug("", "Loading " + spawnerJSONs.size() + " JSON Spawners...");
		for(String spawnerJSONName : spawnerJSONs.keySet()) {
			try {
				JsonObject spawnerJSON = spawnerJSONs.get(spawnerJSONName);
				LycanitesMobs.printDebug("JSONSpawner", "Loading Spawner JSON: " + spawnerJSON);
				Spawner spawner = new Spawner();
				spawner.loadFromJSON(spawnerJSON);
				this.addSpawner(spawner);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Spawner: " + spawnerJSONName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Spawner: " + spawnerJSONName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("", "Complete! " + this.spawners.size() + " JSON Spawners Loaded In Total.");


		// Load Global Spawn Conditions:
		this.globalSpawnConditions.clear();
		Path defaultGlobalPath = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "globalspawner.json");
		JsonObject defaultGlobalJson = this.loadJsonObject(gson, defaultGlobalPath);

		File customGlobalFile = new File(configPath + "globalspawner.json");
		JsonObject customGlobalJson = null;
		if(customGlobalFile.exists()) {
			customGlobalJson = this.loadJsonObject(gson, customGlobalFile.toPath());
		}

		JsonObject globalJson = this.writeDefaultJSONObject(gson, "globalspawner", defaultGlobalJson, customGlobalJson);
		if(globalJson.has("conditions")) {
			JsonArray jsonArray = globalJson.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject spawnConditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(spawnConditionJson);
				this.globalSpawnConditions.add(spawnCondition);
			}
		}
		if(this.globalSpawnConditions.size() > 0) {
			LycanitesMobs.printDebug("JSONSpawner", "Loaded " + this.globalSpawnConditions.size() + " Global Spawn Conditions.");
		}
	}


	@Override
	public void parseJson(GroupInfo groupInfo, String name, JsonObject json) {

	}


	/** Reloads all JSON Spawners. **/
	public void reload() {
		LycanitesMobs.printDebug("JSONSpawner", "Destroying JSON Spawners!");
		for(Spawner spawner : this.spawners.values().toArray(new Spawner[this.spawners.size()])) {
			spawner.destroy();
		}

		this.loadAllFromJSON();
	}


	/** Adds a new Spawner to this Manager. **/
	public void addSpawner(Spawner spawner) {
		if(this.spawners.containsKey(spawner.name)) {
			LycanitesMobs.printWarning("", "[Spawner Manager] Tried to add a Spawner with a name that is already in use: " + spawner.name);
			return;
		}
		if(this.spawners.values().contains(spawner)) {
			LycanitesMobs.printWarning("", "[Spawner Manager] Tried to add a Spawner that is already added: " + spawner.name);
			return;
		}
		this.spawners.put(spawner.name, spawner);
	}


	/** Removes a Spawner from this Manager. **/
	public void removeSpawner(Spawner spawner) {
		if(!this.spawners.containsKey(spawner.name)) {
			LycanitesMobs.printWarning("", "[Spawner Manager] Tried to remove a Spawner that hasn't been added: " + spawner.name);
			return;
		}
		this.spawners.remove(spawner.name);
	}
}
