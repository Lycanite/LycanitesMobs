package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.renderer.RenderRegister;
import lycanite.lycanitesmobs.forestmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("ent", new ModelEnt());
		AssetManager.addModel("trent", new ModelTrent());
		AssetManager.addModel("shambler", new ModelShambler());
		AssetManager.addModel("arisaur", new ModelArisaur());
        AssetManager.addModel("spriggan", new ModelSpriggan());
        AssetManager.addModel("warg", new ModelWarg());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}