package com.lycanitesmobs.arcticmobs;

import com.lycanitesmobs.arcticmobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.arcticmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("frostweaver", new ModelFrostweaver());
		AssetManager.addModel("yeti", new ModelYeti());
		AssetManager.addModel("wendigo", new ModelWendigo());
        AssetManager.addModel("arix", new ModelArix());
        AssetManager.addModel("serpix", new ModelSerpix());
		AssetManager.addModel("maug", new ModelMaug());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}