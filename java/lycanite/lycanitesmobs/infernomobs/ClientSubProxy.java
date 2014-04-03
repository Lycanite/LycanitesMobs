package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.infernomobs.model.ModelCinder;
import lycanite.lycanitesmobs.infernomobs.model.ModelLobber;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Cinder", new ModelCinder());
		AssetManager.addModel("Lobber", new ModelLobber());
	}
}