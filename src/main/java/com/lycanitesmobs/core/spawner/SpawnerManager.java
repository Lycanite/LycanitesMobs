package com.lycanitesmobs.core.spawner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpawnerManager {
	/** This manages all Spawners, it load them and can also destroy them. Spawners are then ran by Spawn Triggers which are called from the SpawnerEventListener. **/

	public static SpawnerManager INSTANCE;

	public Map<String, Spawner> spawners = new HashMap<>();


	/** Loads (or reloads) all JSON Spawners. **/
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

		// Load Default Spawners:
		Path defaultSpawnerPath = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "spawners");
		try {
			Iterator<Path> iterator = Files.walk(defaultSpawnerPath).iterator();
			while(iterator.hasNext()) {
				Path filePath = iterator.next();
				Path relativePath = defaultSpawnerPath.relativize(filePath);
				if (!"json".equals(FilenameUtils.getExtension(filePath.toString()))) {
					continue;
				}
				BufferedReader reader = null;
				try {
					try {
						reader = Files.newBufferedReader(filePath);
						JsonObject spawnerJSON = JsonUtils.fromJson(gson, reader, JsonObject.class);
						LycanitesMobs.printDebug("JSONSpawner", "Loading Spawner JSON: " + spawnerJSON + "...");
						Spawner spawner = new Spawner();
						spawner.loadFromJSON(spawnerJSON);
						this.addSpawner(spawner);
					}
					catch (JsonParseException e) {
						LycanitesMobs.printWarning("", "Parsing error loading JSON Spawner " + relativePath + "\n" + e.toString());
						e.printStackTrace();
					}
					catch (Exception e) {
						LycanitesMobs.printWarning("", "There was a problem loading JSON Spawner " + relativePath + "\n" + e.toString());
						e.printStackTrace();
					}
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "[Spawner Manager] Unable to read files from default spawner directory.\n" + e.toString());
			e.printStackTrace();
		}


		// Load Default Mob Event Spawners:
		Path defaultMobEventPath = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "mobevents");
		List<ResourceLocation> defaultMobEventResourceLocations = Utilities.getPathResourceLocations(defaultMobEventPath, LycanitesMobs.group.filename, "json");

		// Load Custom:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customSpawnersDir = new File(configPath + "spawners");
		File customMobEventsDir = new File(configPath + "mobevents");

		// Create Spawners:
		// TODO Create Spawners from the loaded JSON.
		// Check custom first, if default is false, ignore default. If default is true, load default and overwrite the config file.
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
