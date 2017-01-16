package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.renderer.RenderRegister;
import lycanite.lycanitesmobs.plainsmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerRenders(GroupInfo groupInfo) {
        // Add Models:
		AssetManager.addModel("kobold", new ModelKobold());
		AssetManager.addModel("ventoraptor", new ModelVentoraptor());
		AssetManager.addModel("maka", new ModelMaka());
		AssetManager.addModel("makaalpha", new ModelMakaAlpha());
		AssetManager.addModel("zoataur", new ModelZoataur());
		AssetManager.addModel("roc", new ModelRoc());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}