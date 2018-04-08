package com.lycanitesmobs.elementalmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.elementalmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		// Mobs:
		AssetManager.addModel("cinder", new ModelCinder());
		AssetManager.addModel("jengu", new ModelJengu());
		AssetManager.addModel("geonach", new ModelGeonach());
		AssetManager.addModel("djinn", new ModelDjinn());
		AssetManager.addModel("aegis", new ModelAegis());
		AssetManager.addModel("argus", new ModelArgus());

		AssetManager.addModel("xaphan", new ModelXaphan());
		AssetManager.addModel("volcan", new ModelVolcan());
		AssetManager.addModel("zephyr", new ModelZephyr());
		AssetManager.addModel("grue", new ModelGrue());
		AssetManager.addModel("spriggan", new ModelSpriggan());
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("nymph", new ModelNymph());
		AssetManager.addModel("eechetik", new ModelEechetik());
		AssetManager.addModel("vapula", new ModelVapula());
		AssetManager.addModel("tremor", new ModelTremor());
		AssetManager.addModel("wraith", new ModelWraith());
		AssetManager.addModel("spectre", new ModelSpectre());

		// Projectiles:
		AssetManager.addModel("crystalshard", new ModelCrystalShard());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}