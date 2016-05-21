package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.renderer.RenderRegister;
import lycanite.lycanitesmobs.demonmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("belph", new ModelBelph());
		AssetManager.addModel("behemoth", new ModelBehemoth());
		AssetManager.addModel("pinky", new ModelPinky());
		AssetManager.addModel("trite", new ModelTrite());
		AssetManager.addModel("astaroth", new ModelAstaroth());
		AssetManager.addModel("nethersoul", new ModelNetherSoul());
		AssetManager.addModel("cacodemon", new ModelCacodemon());
        AssetManager.addModel("rahovart", new ModelRahovart());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}