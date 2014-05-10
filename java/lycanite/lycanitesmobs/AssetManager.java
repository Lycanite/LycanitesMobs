package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class AssetManager {
	
	// Maps:
	public static Map<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();
	public static Map<String, IIcon> icons = new HashMap<String, IIcon>();
	public static Map<String, IIcon[]> iconGroups = new HashMap<String, IIcon[]>();
	public static Map<String, String> sounds = new HashMap<String, String>();
	public static Map<String, ModelBase> models = new HashMap<String, ModelBase>();
	public static Map<String, IModelCustom> objModels = new HashMap<String, IModelCustom>();
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Texture ==========
	public static void addTexture(String name, String domain, String path) {
		name = name.toLowerCase();
		textures.put(name, new ResourceLocation(domain, path));
	}
	
	// ========== Icon ==========
	public static void addIcon(String name, IIcon icon) {
		name = name.toLowerCase();
		icons.put(name, icon);
	}
	public static void addIcon(String name, String domain, String path, IIconRegister iconRegister) {
		name = name.toLowerCase();
		icons.put(name, iconRegister.registerIcon(domain + ":" + path));
	}
	
	// ========== Icon Group ==========
	public static void addIconGroup(String name, String domain, String[] paths, IIconRegister iconRegister) {
		name = name.toLowerCase();
		IIcon[] iconGroup = new IIcon[paths.length];
		for(int i = 0; i < paths.length; i++)
			iconGroup[i] = iconRegister.registerIcon(domain + ":" + paths[i]);
		iconGroups.put(name, iconGroup);
	}
	
	// ========== Sound ==========
	public static void addSound(String name, String domain, String path) {
		name = name.toLowerCase();
		sounds.put(name, domain + ":" + path);
	}
	
	// ========== Model ==========
	public static void addModel(String name, ModelBase model) {
		name = name.toLowerCase();
		models.put(name, model);
	}
	
	// ========== Obj Model ==========
	public static void addObjModel(String name, String domain, String path) {
		name = name.toLowerCase();
		objModels.put(name, AdvancedModelLoader.loadModel(new ResourceLocation(domain, "models/" + path + ".obj")));
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
	
	// ========== Icon ==========
	public static IIcon getIcon(String name) {
		name = name.toLowerCase();
		if(!icons.containsKey(name))
			return null;
		return icons.get(name);
	}
	
	// ========== Icon Group ==========
	public static IIcon[] getIconGroup(String name) {
		name = name.toLowerCase();
		if(!iconGroups.containsKey(name))
			return null;
		return iconGroups.get(name);
	}
	
	// ========== Sound ==========
	public static String getSound(String name) {
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
	public static IModelCustom getObjModel(String name) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			return null;
		return objModels.get(name);
	}
	public static IModelCustom getObjModel(String name, String domain, String path) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			addObjModel(name, domain, path);
		return objModels.get(name);
	}
}
