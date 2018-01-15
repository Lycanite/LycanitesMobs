package com.lycanitesmobs.freshwatermobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.freshwatermobs.model.*;

public class ClientSubProxy extends CommonSubProxy {

	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
        AssetManager.addModel("strider", new ModelStrider());
        AssetManager.addModel("silex", new ModelSilex());
        AssetManager.addModel("thresher", new ModelThresher());
        AssetManager.addModel("ioray", new ModelIoray());
        AssetManager.addModel("abaia", new ModelAbaia());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}