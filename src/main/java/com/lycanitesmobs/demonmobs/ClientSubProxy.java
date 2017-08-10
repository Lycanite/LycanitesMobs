package com.lycanitesmobs.demonmobs;

import com.lycanitesmobs.demonmobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.demonmobs.model.*;

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
        AssetManager.addModel("asmodeus", new ModelAsmodeus());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}