package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.saltwatermobs.model.ModelLacedon;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Lacedon", new ModelLacedon());
	}
}