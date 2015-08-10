package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.demonmobs.model.*;
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
		AssetManager.addModel("astaroth", new ModelAstaroth());
		AssetManager.addModel("nethersoul", new ModelNetherSoul());
		AssetManager.addModel("cacodemon", new ModelCacodemon());
        AssetManager.addModel("rahovart", new ModelRahovart());
	}
}