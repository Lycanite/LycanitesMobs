package com.lycanitesmobs.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.util.JsonUtils;
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
import java.util.Map;

public abstract class JSONLoader {
	/**
	 * Loads all JSON files into this manager. Should only be done on pre-init.
	 * @param groupInfo The group that this manager should load from.
	 * @param name The name of this manager, used for debug keys and logging, etc.
	 * @param assetPath The path to load json files from relative to the group assets folder and the config folder.
	 * @param mapKey The json value to use as the map key, usually the "name" field.
	 * @param loadCustom If true, additional custom json files will also be loaded from the config directory for adding custom entries.
	 */
	public void loadAllJson(GroupInfo groupInfo, String name, String assetPath, String mapKey, boolean loadCustom) {
		LycanitesMobs.printDebug(name, "Loading JSON " + name + "...");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> jsons = new HashMap<>();

		// Load Default:
		Path path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.filename, assetPath);
		Map<String, JsonObject> defaultJsons = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultJsons, mapKey, null);

		// Load Custom:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customDir = new File(configPath + assetPath);
		customDir.mkdirs();
		path = customDir.toPath();
		Map<String, JsonObject> customJsons = new HashMap<>();
		this.loadJsonObjects(gson, path, customJsons, mapKey, null);


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultJsons, customJsons, jsons, loadCustom, assetPath);


		// Parse Json:
		LycanitesMobs.printDebug(name, "Loading " + jsons.size() + " " + name + "...");
		for(String jsonName : jsons.keySet()) {
			try {
				JsonObject json = jsons.get(jsonName);
				LycanitesMobs.printDebug(name, "Loading " + name + " JSON: " + json);
				this.parseJson(groupInfo, name, json);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON " + name + ": " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON " + name + ": " + jsonName);
				e.printStackTrace();
			}
		}
	}


	/**
	 * Reads a JSON object and adds it to this JSON Loader.
	 * @param groupInfo The Group Info to load with.
	 * @param json The Json group name.
	 * @param json The Json Object to read.
	 */
	public abstract void parseJson(GroupInfo groupInfo, String name, JsonObject json);


	/**
	 * Loads JSON objects from the specified path with additional options.
	 * @param gson The JSON parser.
	 * @param path The path to load from.
	 * @param jsonObjectMap The map to add the loaded JSON to.
	 * @param mapKey The JSON value to use as the map key.
	 * @param jsonType If set, a "type" value is checked in the JSON and must match.
	 */
	public void loadJsonObjects(Gson gson, Path path, Map<String, JsonObject> jsonObjectMap, String mapKey, String jsonType) {
		if(path == null) {
			return;
		}
		try {
			Iterator<Path> iterator = Files.walk(path).iterator();
			while(iterator.hasNext()) {
				Path filePath = iterator.next();
				Path relativePath = path.relativize(filePath);
				if (!"json".equals(FilenameUtils.getExtension(filePath.toString()))) {
					continue;
				}
				BufferedReader reader = null;
				try {
					try {
						reader = Files.newBufferedReader(filePath);
						JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
						boolean validJSON = true;
						if(jsonType != null) {
							if(!json.has("type")) {
								validJSON = false;
							}
							else {
								validJSON = jsonType.equalsIgnoreCase(json.get("type").getAsString());
							}
						}
						if(validJSON) {
							jsonObjectMap.put(json.get(mapKey).getAsString(), json);
						}
					}
					catch (JsonParseException e) {
						LycanitesMobs.printWarning("", "Parsing error loading JSON " + relativePath + "\n" + e.toString());
						e.printStackTrace();
					}
					catch (Exception e) {
						LycanitesMobs.printWarning("", "There was a problem loading JSON " + relativePath + "\n" + e.toString());
						e.printStackTrace();
					}
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "Unable to read files from directory.\n" + e.toString());
			//e.printStackTrace();
		}
	}


	/** Cycles through both maps of JSON Objects, a default and a custom map and determines if the defaults should overwrite the custom JSON. Puts the chosen JSON into the mixed map. **/
	public void writeDefaultJSONObjects(Gson gson, Map<String, JsonObject> defaultJSONs, Map<String, JsonObject> customJSONs, Map<String, JsonObject> mixedJSONs, boolean custom, String assetPath) {
		// Add Default/Overridden JSON:
		for(String jsonName : defaultJSONs.keySet()) {
			try {
				JsonObject defaultJSON = defaultJSONs.get(jsonName);
				boolean loadDefault = true;

				// If Custom Replacement Exists:
				JsonObject customJSON = null;
				if(customJSONs.containsKey(jsonName)) {
					loadDefault = false;
					customJSON = customJSONs.get(jsonName);
					if(customJSON.has("loadDefault")) {
						loadDefault = customJSON.get("loadDefault").getAsBoolean();
					}
				}

				// Write Default:
				if(loadDefault) {
					this.saveJsonObject(gson, defaultJSON, jsonName, assetPath);
					mixedJSONs.put(jsonName, defaultJSON);
				}
				else if(customJSON != null) {
					mixedJSONs.put(jsonName, customJSON);
				}
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON: " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON: " + jsonName);
				e.printStackTrace();
			}
		}

		// Add Custom JSON:
		if(custom) {
			for (String jsonName : customJSONs.keySet()) {
				if (!defaultJSONs.containsKey(jsonName)) {
					mixedJSONs.put(jsonName, customJSONs.get(jsonName));
				}
			}
		}
	}

	/**
	 * Loads a JSON object from the specified path with additional options.
	 * @param gson The JSON parser.
	 * @param path The path to load from.
	 * @return An instance of the json object.
	 */
	public JsonObject loadJsonObject(Gson gson, Path path) {
		if(path == null) {
			return null;
		}
		try {
			Path relativePath = path.relativize(path);
			BufferedReader reader = null;
			try {
				try {
					reader = Files.newBufferedReader(path);
					JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
					return json;
				}
				catch (JsonParseException e) {
					LycanitesMobs.printWarning("", "Parsing error loading JSON " + relativePath + "\n" + e.toString());
					e.printStackTrace();
				}
				catch (Exception e) {
					LycanitesMobs.printWarning("", "There was a problem loading JSON " + relativePath + "\n" + e.toString());
					e.printStackTrace();
				}
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "Unable to read file from path.\n" + e.toString());
			//e.printStackTrace();
		}
		return null;
	}


	/** Compares two json objects a default and a custom and determines if the defaults should overwrite the custom JSON. Returns the chosen JSON. **/
	public JsonObject writeDefaultJSONObject(Gson gson, String jsonName, JsonObject defaultJSON, JsonObject customJSON) {
		// Add Default/Overridden JSON:
		try {
			boolean loadDefault = true;

			// If Custom Replacement Exists:
			if(customJSON != null) {
				loadDefault = false;
				if(customJSON.has("loadDefault")) {
					loadDefault = customJSON.get("loadDefault").getAsBoolean();
				}
			}

			// Write Default:
			if(loadDefault) {
				this.saveJsonObject(gson, defaultJSON, jsonName, "");
				return defaultJSON;
			}
			else if(customJSON != null) {
				return customJSON;
			}
		}
		catch (JsonParseException e) {
			LycanitesMobs.printWarning("", "Parsing error loading JSON: " + jsonName);
			e.printStackTrace();
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "There was a problem loading JSON: " + jsonName);
			e.printStackTrace();
		}
		return null;
	}


	/** Saves a JSON object into the config folder. **/
	public void saveJsonObject(Gson gson, JsonObject jsonObject, String name, String assetPath) {
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		try {
			File jsonFile = new File(configPath + (!"".equals(assetPath) ? assetPath + "/" : "") + name + ".json");
			jsonFile.getParentFile().mkdirs();
			jsonFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(jsonFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.append(gson.toJson(jsonObject));
			outputStreamWriter.close();
			outputStream.close();
		}
		catch (Exception e) {
			LycanitesMobs.printWarning("", "Unable to save JSON into the config folder.");
			e.printStackTrace();
		}
	}
}
