package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.event.ForgeSubscribe;

public class AssetManager {
	
	// Maps:
	public static Map<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();
	public static Map<String, Icon> icons = new HashMap<String, Icon>();
	public static Map<String, Icon[]> iconGroups = new HashMap<String, Icon[]>();
	public static Map<String, String[]> sounds = new HashMap<String, String[]>();
	public static Map<String, ModelBase> models = new HashMap<String, ModelBase>();
	public static Map<String, IModelCustom> objModels = new HashMap<String, IModelCustom>();
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Texture ==========
	public static void addTexture(String name, String domain, String path) {
		textures.put(name, new ResourceLocation(domain, path));
	}
	
	// ========== Icon ==========
	public static void addIcon(String name, Icon icon) {
		icons.put(name, icon);
	}
	public static void addIcon(String name, String domain, String path, IconRegister iconRegister) {
		icons.put(name, iconRegister.registerIcon(domain + ":" + path));
	}
	
	// ========== Icon Group ==========
	public static void addIconGroup(String name, String domain, String[] paths, IconRegister iconRegister) {
		Icon[] iconGroup = new Icon[paths.length];
		for(int i = 0; i < paths.length; i++)
			iconGroup[i] = iconRegister.registerIcon(domain + ":" + paths[i]);
		iconGroups.put(name, iconGroup);
	}
	
	// ========== Sound ==========
	public static void addSound(String name, String domain, String path) {
		sounds.put(name, new String[] {domain + ":" + path, domain + ":" + path.replaceAll("/", ".").substring(0, path.length() - 4)});
	}
	
	// ========== Model ==========
	public static void addModel(String name, ModelBase model) {
		models.put(name, model);
	}
	
	// ========== Obj Model ==========
	public static void addObjModel(String name, String domain, String path) {
		objModels.put(name, AdvancedModelLoader.loadModel("/assets/" + domain + "/models/" + path + ".obj"));
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Texture ==========
	public static ResourceLocation getTexture(String name) {
		if(!textures.containsKey(name))
			return null;
		return textures.get(name);
	}
	
	// ========== Icon ==========
	public static Icon getIcon(String name) {
		if(!icons.containsKey(name))
			return null;
		return icons.get(name);
	}
	
	// ========== Icon Group ==========
	public static Icon[] getIconGroup(String name) {
		if(!iconGroups.containsKey(name))
			return null;
		return iconGroups.get(name);
	}
	
	// ========== Sound ==========
	public static String getSound(String name) {
		if(!sounds.containsKey(name))
			return null;
		return sounds.get(name)[1];
	}
	
	// ========== Model ==========
	public static ModelBase getModel(String name) {
		if(!models.containsKey(name))
			return null;
		return models.get(name);
	}
	
	// ========== Obj Model ==========
	public static IModelCustom getObjModel(String name) {
		if(!objModels.containsKey(name))
			return null;
		return objModels.get(name);
	}
	public static IModelCustom getObjModel(String name, String domain, String path) {
		if(!objModels.containsKey(name))
			addObjModel(name, domain, path);
		return objModels.get(name);
	}
	
	
    // ==================================================
    //                       Register
    // ==================================================
	// ========== Sound ==========
	@ForgeSubscribe
	public void onLoadSoundSettings(SoundLoadEvent soundLoadEvent) {
        for(String[] sound : sounds.values())
			soundLoadEvent.manager.addSound(sound[0]);
    }
}
