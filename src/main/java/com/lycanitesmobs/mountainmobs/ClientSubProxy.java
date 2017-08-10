package com.lycanitesmobs.mountainmobs;

import com.lycanitesmobs.mountainmobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.mountainmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("jabberwock", new ModelJabberwock());
		AssetManager.addModel("troll", new ModelTroll());
		AssetManager.addModel("yale", new ModelYale());
		AssetManager.addModel("geonach", new ModelGeonach());
		AssetManager.addModel("beholder", new ModelBeholder());
		AssetManager.addModel("barghest", new ModelBarghest());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}