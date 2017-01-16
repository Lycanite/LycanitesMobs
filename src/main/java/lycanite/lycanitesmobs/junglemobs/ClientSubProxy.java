package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.renderer.RenderRegister;
import lycanite.lycanitesmobs.junglemobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("geken", new ModelGeken());
		AssetManager.addModel("uvaraptor", new ModelUvaraptor());
		AssetManager.addModel("concapede", new ModelConcapedeHead());
		AssetManager.addModel("concapedesegment", new ModelConcapedeSegment());
		AssetManager.addModel("tarantula", new ModelTarantula());
		AssetManager.addModel("conba", new ModelConba());
		AssetManager.addModel("vespid", new ModelVespid());
		AssetManager.addModel("vespidqueen", new ModelVespidQueen());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}