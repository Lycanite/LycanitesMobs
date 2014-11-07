package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.junglemobs.model.ModelConba;
import lycanite.lycanitesmobs.junglemobs.model.ModelConcapedeHead;
import lycanite.lycanitesmobs.junglemobs.model.ModelConcapedeSegment;
import lycanite.lycanitesmobs.junglemobs.model.ModelGeken;
import lycanite.lycanitesmobs.junglemobs.model.ModelTarantula;
import lycanite.lycanitesmobs.junglemobs.model.ModelUvaraptor;
import lycanite.lycanitesmobs.junglemobs.model.ModelVespid;
import lycanite.lycanitesmobs.junglemobs.model.ModelVespidQueen;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("geken", new ModelGeken());
		AssetManager.addModel("uvaraptor", new ModelUvaraptor());
		AssetManager.addModel("concapede", new ModelConcapedeHead());
		AssetManager.addModel("concapedesegment", new ModelConcapedeSegment());
		AssetManager.addModel("tarantula", new ModelTarantula());
		AssetManager.addModel("conba", new ModelConba());
		AssetManager.addModel("vespid", new ModelVespid());
		AssetManager.addModel("vespidqueen", new ModelVespidQueen());
	}
}