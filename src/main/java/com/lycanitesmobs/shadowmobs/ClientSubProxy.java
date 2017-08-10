package com.lycanitesmobs.shadowmobs;

import com.lycanitesmobs.shadowmobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.shadowmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("grue", new ModelGrue());
		AssetManager.addModel("phantom", new ModelPhantom());
		AssetManager.addModel("epion", new ModelEpion());
        AssetManager.addModel("geist", new ModelGeist());
        AssetManager.addModel("chupacabra", new ModelChupacabra());
        AssetManager.addModel("shade", new ModelShade());
        AssetManager.addModel("darkling", new ModelDarkling());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}