package com.lycanitesmobs.freshwatermobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.freshwatermobs.model.ModelJengu;
import com.lycanitesmobs.freshwatermobs.model.ModelSilex;
import com.lycanitesmobs.freshwatermobs.model.ModelStrider;
import com.lycanitesmobs.freshwatermobs.model.ModelZephyr;

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