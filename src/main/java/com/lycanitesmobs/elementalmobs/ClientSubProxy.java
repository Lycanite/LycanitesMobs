package com.lycanitesmobs.elementalmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.elementalmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("cinder", new ModelCinder());
		AssetManager.addModel("jengu", new ModelJengu());
		AssetManager.addModel("geonach", new ModelGeonach());

		AssetManager.addModel("volcan", new ModelVolcan());
		AssetManager.addModel("zephyr", new ModelZephyr());
		AssetManager.addModel("grue", new ModelGrue());
		AssetManager.addModel("spriggan", new ModelSpriggan());
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("tremor", new ModelTremor());
		AssetManager.addModel("wraith", new ModelWraith());
		AssetManager.addModel("spectre", new ModelSpectre());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}