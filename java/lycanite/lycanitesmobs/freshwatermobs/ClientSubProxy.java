package lycanite.lycanitesmobs.freshwatermobs;

import cpw.mods.fml.client.registry.RenderingRegistry;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.forestmobs.CommonSubProxy;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelJengu;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelZephyr;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("jengu", new ModelJengu());
		AssetManager.addModel("zephyr", new ModelZephyr());
	}
}