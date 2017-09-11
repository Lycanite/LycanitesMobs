package com.lycanitesmobs.saltwatermobs;

import com.lycanitesmobs.saltwatermobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.saltwatermobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("lacedon", new ModelLacedon());
		AssetManager.addModel("skylus", new ModelSkylus());
        AssetManager.addModel("ika", new ModelIka());
        AssetManager.addModel("abtu", new ModelAbtu());
		AssetManager.addModel("raiko", new ModelRaiko());
		AssetManager.addModel("roa", new ModelRoa());
		AssetManager.addModel("herma", new ModelHerma());
		AssetManager.addModel("quetzodracl", new ModelQuetzodracl());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}