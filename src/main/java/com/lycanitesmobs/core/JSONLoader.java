package com.lycanitesmobs.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class JSONLoader {

	/** Loads JSON Objects from a Path. **/
	public void loadJsonObjects(Gson gson, Path path, Map<String, JsonObject> jsonObjectMap, String mapKey) {
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
						jsonObjectMap.put(json.get(mapKey).getAsString(), json);
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
			e.printStackTrace();
		}
	}


	/** Cycles through both maps of JSON Objects, a default and a custom map and determines if the defaults should overwrite the custom JSON. Puts the chosen JSON into the mixed map. **/
	public void writeDefaultJSONObjects(Gson gson, Map<String, JsonObject> defaultJSONs, Map<String, JsonObject> customJSONs, Map<String, JsonObject> mixedJSONs, boolean custom, String groupName) {
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
					this.saveJsonObject(gson, defaultJSON, jsonName, groupName);
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


	/** Saves a JSON object into the config folder. **/
	public void saveJsonObject(Gson gson, JsonObject jsonObject, String name, String groupName) {
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		try {
			File jsonFile = new File(configPath + groupName + "/" + name + ".json");
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
