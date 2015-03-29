package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.forestmobs.model.*;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("ent", new ModelEnt());
		AssetManager.addModel("trent", new ModelTrent());
		AssetManager.addModel("shambler", new ModelShambler());
		AssetManager.addModel("arisaur", new ModelArisaur());
        AssetManager.addModel("spriggan", new ModelSpriggan());
        AssetManager.addModel("warg", new ModelWarg());
	}
}