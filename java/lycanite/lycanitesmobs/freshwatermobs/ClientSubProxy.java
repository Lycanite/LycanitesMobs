package lycanite.lycanitesmobs.freshwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelJengu;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelSilex;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelStrider;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelZephyr;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("jengu", new ModelJengu());
		AssetManager.addModel("zephyr", new ModelZephyr());
        AssetManager.addModel("strider", new ModelStrider());
        AssetManager.addModel("silex", new ModelSilex());
	}
}