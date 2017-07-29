package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.renderer.RenderRegister;
import lycanite.lycanitesmobs.saltwatermobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("lacedon", new ModelLacedon());
		AssetManager.addModel("skylus", new ModelSkylus());
        AssetManager.addModel("ika", new ModelIka());
        AssetManager.addModel("abtu", new ModelAbtu());
		AssetManager.addModel("raiko", new ModelRaiko());
		AssetManager.addModel("roa", new ModelRoa());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}