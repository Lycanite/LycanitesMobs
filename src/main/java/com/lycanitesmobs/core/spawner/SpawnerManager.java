package com.lycanitesmobs.core.spawner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpawnerManager extends JSONLoader {
	/** This manages all Spawners, it load them and can also destroy them. Spawners are then ran by Spawn Triggers which are called from the SpawnerEventListener. **/

	public static SpawnerManager INSTANCE;

	public Map<String, Spawner> spawners = new HashMap<>();


	/** Returns the main SpawnerManager instance or creates it and returns it. **/
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
		this.loadJsonObjects(gson, path, defaultSpawnerJSONs, "name");

		// Load Default Mob Event Spawners:
		//path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "mobevents");
		//Map<String, JsonObject> defaultMobEventsJSONs = new HashMap<>();
		//this.loadJsonObjects(gson, path, defaultMobEventsJSONs, "name");

		// Custom:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";

		// Load Custom Spawners:
		File customSpawnersDir = new File(configPath + "spawners");
		customSpawnersDir.mkdirs();
		path = customSpawnersDir.toPath();
		Map<String, JsonObject> customSpawnerJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customSpawnerJSONs, "name");

		//File customMobEventsDir = new File(configPath + "mobevents");
		//path = customMobEventsDir.toPath();
		//Map<String, JsonObject> customMobEventsJSONs = new HashMap<>();
		//this.loadJsonObjects(gson, path, customMobEventsJSONs, "name");


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultSpawnerJSONs, customSpawnerJSONs, spawnerJSONs, true, "spawners");
		//this.writeDefaultJSONObjects(gson, defaultMobEventsJSONs, customMobEventsJSONs, spawnerJSONs, "mobevents");


		// Create Spawners:
		LycanitesMobs.printDebug("JSONSpawner", "Loading " + spawnerJSONs.size() + " JSON Spawners...");
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
		LycanitesMobs.printDebug("JSONSpawner", "Complete! " + this.spawners.size() + " JSON Spawners Loaded.");
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
