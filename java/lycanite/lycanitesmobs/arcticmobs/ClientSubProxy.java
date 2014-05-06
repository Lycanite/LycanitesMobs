package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.arcticmobs.model.ModelFrostweaver;
import lycanite.lycanitesmobs.arcticmobs.model.ModelReiver;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("frostweaver", new ModelFrostweaver());
	}
}