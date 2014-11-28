package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.infernomobs.model.ModelAfrit;
import lycanite.lycanitesmobs.infernomobs.model.ModelCephignis;
import lycanite.lycanitesmobs.infernomobs.model.ModelCinder;
import lycanite.lycanitesmobs.infernomobs.model.ModelKhalk;
import lycanite.lycanitesmobs.infernomobs.model.ModelLobber;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("cinder", new ModelCinder());
		AssetManager.addModel("lobber", new ModelLobber());
		AssetManager.addModel("cephignis", new ModelCephignis());
        AssetManager.addModel("afrit", new ModelAfrit());
        AssetManager.addModel("khalk", new ModelKhalk());
	}
}