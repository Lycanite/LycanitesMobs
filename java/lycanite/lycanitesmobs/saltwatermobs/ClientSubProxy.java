package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.saltwatermobs.model.ModelLacedon;
import lycanite.lycanitesmobs.saltwatermobs.model.ModelSkylus;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("lacedon", new ModelLacedon());
		AssetManager.addModel("skylus", new ModelSkylus());
	}
}