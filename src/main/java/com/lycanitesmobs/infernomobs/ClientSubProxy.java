package com.lycanitesmobs.infernomobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.infernomobs.model.*;
import com.lycanitesmobs.infernomobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("lobber", new ModelLobber());
		AssetManager.addModel("cephignis", new ModelCephignis());
        AssetManager.addModel("afrit", new ModelAfrit());
        AssetManager.addModel("khalk", new ModelKhalk());
        AssetManager.addModel("salamander", new ModelSalamander());
        AssetManager.addModel("gorger", new ModelGorger());
        AssetManager.addModel("ignibus", new ModelIgnibus());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}