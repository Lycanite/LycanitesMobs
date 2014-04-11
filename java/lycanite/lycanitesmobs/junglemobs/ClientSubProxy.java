package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.junglemobs.model.ModelConcapedeHead;
import lycanite.lycanitesmobs.junglemobs.model.ModelConcapedeSegment;
import lycanite.lycanitesmobs.junglemobs.model.ModelGeken;
import lycanite.lycanitesmobs.junglemobs.model.ModelTarantula;
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
		AssetManager.addModel("Concapede", new ModelConcapedeHead());
		AssetManager.addModel("ConcapedeSegment", new ModelConcapedeSegment());
		AssetManager.addModel("Tarantula", new ModelTarantula());
	}
}