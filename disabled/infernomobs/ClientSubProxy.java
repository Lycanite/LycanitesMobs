package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.infernomobs.model.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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