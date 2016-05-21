package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.renderer.RenderRegister;
import lycanite.lycanitesmobs.desertmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("cryptzombie", new ModelCryptZombie());
		AssetManager.addModel("crusk", new ModelCrusk());
		AssetManager.addModel("clink", new ModelClink());
		AssetManager.addModel("joust", new ModelJoust());
		AssetManager.addModel("joustalpha", new ModelJoustAlpha());
		AssetManager.addModel("erepede", new ModelErepede());
		AssetManager.addModel("gorgomite", new ModelGorgomite());
		AssetManager.addModel("manticore", new ModelManticore());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}