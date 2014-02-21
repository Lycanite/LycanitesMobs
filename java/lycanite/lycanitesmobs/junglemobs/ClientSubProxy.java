package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.junglemobs.model.ModelGeken;
import lycanite.lycanitesmobs.junglemobs.model.ModelUvaraptor;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Geken", new ModelGeken());
		AssetManager.addModel("Uvaraptor", new ModelUvaraptor());
	}
}