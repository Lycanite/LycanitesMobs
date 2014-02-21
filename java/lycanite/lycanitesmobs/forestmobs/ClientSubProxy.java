package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.forestmobs.model.ModelEnt;
import lycanite.lycanitesmobs.forestmobs.model.ModelTrent;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Ent", new ModelEnt());
		AssetManager.addModel("Trent", new ModelTrent());
	}
}