package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.renderer.RenderRegister;
import lycanite.lycanitesmobs.arcticmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("frostweaver", new ModelFrostweaver());
		AssetManager.addModel("yeti", new ModelYeti());
		AssetManager.addModel("wendigo", new ModelWendigo());
        AssetManager.addModel("arix", new ModelArix());
        AssetManager.addModel("serpix", new ModelSerpix());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}