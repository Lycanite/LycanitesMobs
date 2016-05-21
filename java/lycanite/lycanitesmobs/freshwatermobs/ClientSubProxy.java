package lycanite.lycanitesmobs.freshwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.renderer.RenderRegister;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelJengu;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelSilex;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelStrider;
import lycanite.lycanitesmobs.freshwatermobs.model.ModelZephyr;

public class ClientSubProxy extends CommonSubProxy {

	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("jengu", new ModelJengu());
		AssetManager.addModel("zephyr", new ModelZephyr());
        AssetManager.addModel("strider", new ModelStrider());
        AssetManager.addModel("silex", new ModelSilex());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}