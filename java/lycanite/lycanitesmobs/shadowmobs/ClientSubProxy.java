package lycanite.lycanitesmobs.shadowmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.shadowmobs.model.*;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("grue", new ModelGrue());
		AssetManager.addModel("phantom", new ModelPhantom());
		AssetManager.addModel("epion", new ModelEpion());
        AssetManager.addModel("geist", new ModelGeist());
        AssetManager.addModel("chupacabra", new ModelChupacabra());
        AssetManager.addModel("shade", new ModelShade());
	}
}