package com.lycanitesmobs;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.ModelCustomObj;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.lycanitesmobs.RecipeExporter.RecipeExporter;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {
	
	// Maps:
	public static Map<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();
	public static Map<String, ResourceLocation[]> textureGroups = new HashMap<String, ResourceLocation[]>();
	public static Map<String, SoundEvent> sounds = new HashMap<String, SoundEvent>();
	public static Map<String, ModelBase> models = new HashMap<String, ModelBase>();
	public static Map<String, IModelCustomData> objModels = new HashMap<String, IModelCustomData>();
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Texture ==========
	public static void addTexture(String name, GroupInfo group, String path) {
		name = name.toLowerCase();
		textures.put(name, new ResourceLocation(group.filename, path));
	}
	
	// ========== Texture Group ==========
	public static void addTextureGroup(String name, GroupInfo group, String[] paths) {
		name = name.toLowerCase();
        ResourceLocation[] textureGroup = new ResourceLocation[paths.length];
		for(int i = 0; i < paths.length; i++)
            textureGroup[i] = new ResourceLocation(group.filename, paths[i]);
        textureGroups.put(name, textureGroup);
	}
	
	// ========== Sound ==========
	public static void addSound(String name, GroupInfo group, String path) {
		name = name.toLowerCase();
        ResourceLocation resourceLocation = new ResourceLocation(group.filename, path);
        SoundEvent soundEvent = new SoundEvent(resourceLocation);
        soundEvent.setRegistryName(resourceLocation);
		sounds.put(name, soundEvent);
        GameRegistry.register(soundEvent);
	}
	
	// ========== Model ==========
	public static void addModel(String name, ModelBase model) {
		name = name.toLowerCase();
		models.put(name, model);
	}
	
	// ========== Obj Model ==========
	public static void addObjModel(String name, GroupInfo group, String path) {
		name = name.toLowerCase();
		objModels.put(name, ModelCustomObj.loadModel(new ResourceLocation(group.filename, "models/" + path + ".obj")));
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Texture ==========
	public static ResourceLocation getTexture(String name) {
		name = name.toLowerCase();
		if(!textures.containsKey(name))
			return null;
		return textures.get(name);
	}
	
	// ========== Icon Group ==========
	public static ResourceLocation[] getTextureGroup(String name) {
		name = name.toLowerCase();
		if(!textureGroups.containsKey(name))
			return null;
		return textureGroups.get(name);
	}
	
	// ========== Sound ==========
	public static SoundEvent getSound(String name) {
		name = name.toLowerCase();
		if(!sounds.containsKey(name))
			return null;
		return sounds.get(name);
	}
	
	// ========== Model ==========
	public static ModelBase getModel(String name) {
		name = name.toLowerCase();
		if(!models.containsKey(name))
			return null;
		return models.get(name);
	}
	
	// ========== Obj Model ==========
	public static IModelCustomData getObjModel(String name) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			return null;
		return objModels.get(name);
	}
	public static IModelCustomData getObjModel(String name, GroupInfo group, String path) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			addObjModel(name, group, path);
		return objModels.get(name);
	}
}
