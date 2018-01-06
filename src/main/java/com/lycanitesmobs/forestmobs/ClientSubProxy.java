package com.lycanitesmobs.forestmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.forestmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {


	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("ent", new ModelEnt());
		AssetManager.addModel("treant", new ModelTreant());
		AssetManager.addModel("shambler", new ModelShambler());
		AssetManager.addModel("arisaur", new ModelArisaur());
        AssetManager.addModel("spriggan", new ModelSpriggan());
        AssetManager.addModel("warg", new ModelWarg());
		AssetManager.addModel("calpod", new ModelCalpod());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}
