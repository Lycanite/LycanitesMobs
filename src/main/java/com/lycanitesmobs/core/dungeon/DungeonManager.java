package com.lycanitesmobs.core.dungeon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonStructure;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonManager extends JSONLoader {
	/** This manages all Dungeons, it can load them and can also destroy them. **/

	public static DungeonManager INSTANCE;

	public Map<String, DungeonTheme> themes = new HashMap<>();
	public Map<String, DungeonStructure> structures = new HashMap<>();
	public Map<String, DungeonSector> sectors = new HashMap<>();
	public Map<String, DungeonSchematic> schematics = new HashMap<>();


	/** Returns the main DungeonManager INSTANCE or creates it and returns it. **/
	public static DungeonManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DungeonManager();
		}
		return INSTANCE;
	}


	/** Loads all JSON Dungeons. **/
	public void loadAllFromJSON() {
		// Themes:
		Map<String, JsonObject> themeJSONs = this.loadDungeonsFromJSON("themes");
		LycanitesMobs.printDebug("", "Loading " + themeJSONs.size() + " JSON Dungeon Themes...");
		for(String jsonName : themeJSONs.keySet()) {
			try {
				JsonObject json = themeJSONs.get(jsonName);
				LycanitesMobs.printDebug("Dungeon", "Loading Dungeon Themes JSON: " + json);
				DungeonTheme theme = new DungeonTheme();
				theme.loadFromJSON(json);
				this.addTheme(theme);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Dungeon Theme: " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Dungeon Theme: " + jsonName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("", "Complete! " + this.themes.size() + " JSON Dungeon Themes Loaded In Total.");


		// Structures:
		Map<String, JsonObject> structureJSONs = this.loadDungeonsFromJSON("structures");
		LycanitesMobs.printDebug("", "Loading " + structureJSONs.size() + " JSON Dungeon Structures...");
		for(String jsonName : structureJSONs.keySet()) {
			try {
				JsonObject json = structureJSONs.get(jsonName);
				LycanitesMobs.printDebug("Dungeon", "Loading Dungeon Structures JSON: " + json);
				DungeonStructure structure = new DungeonStructure();
				structure.loadFromJSON(json);
				this.addStructure(structure);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Dungeon Structure: " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Dungeon Structure: " + jsonName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("", "Complete! " + this.themes.size() + " JSON Dungeon Structures Loaded In Total.");


		// Sectors:
		Map<String, JsonObject> sectorJSONs = this.loadDungeonsFromJSON("sectors");
		LycanitesMobs.printDebug("", "Loading " + sectorJSONs.size() + " JSON Dungeon Sectors...");
		for(String jsonName : sectorJSONs.keySet()) {
			try {
				JsonObject json = sectorJSONs.get(jsonName);
				LycanitesMobs.printDebug("Dungeon", "Loading Dungeon Sectors JSON: " + json);
				DungeonSector sector = new DungeonSector();
				sector.loadFromJSON(json);
				this.addSector(sector);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Dungeon Sector: " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Dungeon Sector: " + jsonName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("", "Complete! " + this.themes.size() + " JSON Dungeon Sectors Loaded In Total.");


		// Schematics:
		Map<String, JsonObject> schematicJSONs = this.loadDungeonsFromJSON("schematics");
		LycanitesMobs.printDebug("", "Loading " + schematicJSONs.size() + " JSON Dungeon Schematics...");
		for(String jsonName : schematicJSONs.keySet()) {
			try {
				JsonObject json = schematicJSONs.get(jsonName);
				LycanitesMobs.printDebug("Dungeon", "Loading Dungeon Schematics JSON: " + json);
				DungeonSchematic schematic = new DungeonSchematic();
				schematic.loadFromJSON(json);
				this.addSchematic(schematic);
			}
			catch (JsonParseException e) {
				LycanitesMobs.printWarning("", "Parsing error loading JSON Dungeon Schematics: " + jsonName);
				e.printStackTrace();
			}
			catch(Exception e) {
				LycanitesMobs.printWarning("", "There was a problem loading JSON Dungeon Schematics: " + jsonName);
				e.printStackTrace();
			}
		}
		LycanitesMobs.printDebug("", "Complete! " + this.themes.size() + " JSON Dungeon Schematics Loaded In Total.");
	}


	/** Loads all JSON Dungeons. **/
	public Map<String, JsonObject> loadDungeonsFromJSON(String type) {
		LycanitesMobs.printDebug("Dungeon", "Loading JSON Dungeons!");
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		Map<String, JsonObject> spawnerJSONs = new HashMap<>();

		// Load Defaults:
		Path path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.group.filename, "dungeons/" + type);
		Map<String, JsonObject> defaultJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, defaultJSONs, "name", null);

		// Load Custom:
		String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid + "/";
		File customDir = new File(configPath + "dungeons/" + type);
		customDir.mkdirs();
		path = customDir.toPath();
		Map<String, JsonObject> customJSONs = new HashMap<>();
		this.loadJsonObjects(gson, path, customJSONs, "name", null);


		// Write Defaults:
		this.writeDefaultJSONObjects(gson, defaultJSONs, customJSONs, spawnerJSONs, true, "dungeons/" + type);
		return spawnerJSONs;
	}


	/** Reloads all JSON Dungeons. **/
	public void reload() {
		LycanitesMobs.printDebug("Dungeon", "Destroying JSON Dungeons!");
		this.themes.clear();
		this.structures.clear();
		this.sectors.clear();
		this.schematics.clear();

		this.loadAllFromJSON();
	}


	/** Adds a new Dungeon Theme to this Manager. **/
	public void addTheme(DungeonTheme theme) {
		if(this.themes.containsKey(theme.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Theme with a name that is already in use: " + theme.name);
			return;
		}
		if(this.themes.values().contains(theme)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Theme that is already added: " + theme.name);
			return;
		}
		this.themes.put(theme.name, theme);
	}


	/** Removes a Dungeon Theme from this Manager. **/
	public void removeTheme(DungeonTheme theme) {
		if(!this.themes.containsKey(theme.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to remove a Dungeon Theme that hasn't been added: " + theme.name);
			return;
		}
		this.themes.remove(theme.name);
	}


	/** Gets a Theme by name or null if none can be found. **/
	public DungeonTheme getTheme(String name) {
		if(!this.themes.containsKey(name)) {
			LycanitesMobs.printWarning("Dungeon", "Unable to find a theme called " + name);
			return null;
		}
		return this.themes.get(name);
	}


	/** Adds a new Dungeon Structure to this Manager. **/
	public void addStructure(DungeonStructure structure) {
		if(this.structures.containsKey(structure.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Structure with a name that is already in use: " + structure.name);
			return;
		}
		if(this.structures.values().contains(structure)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Structure that is already added: " + structure.name);
			return;
		}
		this.structures.put(structure.name, structure);
	}


	/** Removes a Dungeon Structure from this Manager. **/
	public void removeStructure(DungeonStructure structure) {
		if(!this.structures.containsKey(structure.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to remove a Dungeon Structure that hasn't been added: " + structure.name);
			return;
		}
		this.structures.remove(structure.name);
	}


	/** Gets a Structure by name or null if none can be found. **/
	public DungeonStructure getStructure(String name) {
		if(!this.structures.containsKey(name)) {
			return null;
		}
		return this.structures.get(name);
	}


	/** Adds a new Dungeon Sector to this Manager. **/
	public void addSector(DungeonSector sector) {
		if(this.sectors.containsKey(sector.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Sector with a name that is already in use: " + sector.name);
			return;
		}
		if(this.sectors.values().contains(sector)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Sector that is already added: " + sector.name);
			return;
		}
		this.sectors.put(sector.name, sector);
	}


	/** Removes a Dungeon Sector from this Manager. **/
	public void removeSector(DungeonSector sector) {
		if(!this.sectors.containsKey(sector.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to remove a Dungeon Sector that hasn't been added: " + sector.name);
			return;
		}
		this.sectors.remove(sector.name);
	}


	/** Gets a Sector by name or null if none can be found. **/
	public DungeonSector getSector(String name) {
		if(!this.sectors.containsKey(name)) {
			LycanitesMobs.printWarning("Dungeon", "Unable to find a Dungeon Sector by the name: " + name);
			return null;
		}
		return this.sectors.get(name);
	}


	/** Adds a new Dungeon Schematic to this Manager. **/
	public void addSchematic(DungeonSchematic schematic) {
		if(this.schematics.containsKey(schematic.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Schematic with a name that is already in use: " + schematic.name);
			return;
		}
		if(this.schematics.values().contains(schematic)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to add a Dungeon Schematic that is already added: " + schematic.name);
			return;
		}
		this.schematics.put(schematic.name, schematic);
	}


	/** Removes a Dungeon Schematic from this Manager. **/
	public void removeSchematic(DungeonSchematic schematic) {
		if(!this.schematics.containsKey(schematic.name)) {
			LycanitesMobs.printWarning("", "[Dungeon Manager] Tried to remove a Dungeon Schematic that hasn't been added: " + schematic.name);
			return;
		}
		this.schematics.remove(schematic.name);
	}


	/** Gets a Schematic by name or null if none can be found. **/
	public DungeonSchematic getSchematic(String name) {
		if(!this.schematics.containsKey(name)) {
			return null;
		}
		return this.schematics.get(name);
	}
}
