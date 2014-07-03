package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.demonmobs.model.ModelAsmodi;
import lycanite.lycanitesmobs.demonmobs.model.ModelBehemoth;
import lycanite.lycanitesmobs.demonmobs.model.ModelBelph;
import lycanite.lycanitesmobs.demonmobs.model.ModelCacodemon;
import lycanite.lycanitesmobs.demonmobs.model.ModelNetherSoul;
import lycanite.lycanitesmobs.demonmobs.model.ModelPinky;
import lycanite.lycanitesmobs.demonmobs.model.ModelTrite;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("belph", new ModelBelph());
		AssetManager.addModel("behemoth", new ModelBehemoth());
		AssetManager.addModel("pinky", new ModelPinky());
		AssetManager.addModel("trite", new ModelTrite());
		AssetManager.addModel("asmodi", new ModelAsmodi());
		AssetManager.addModel("nethersoul", new ModelNetherSoul());
		AssetManager.addModel("cacodemon", new ModelCacodemon());
	}
}