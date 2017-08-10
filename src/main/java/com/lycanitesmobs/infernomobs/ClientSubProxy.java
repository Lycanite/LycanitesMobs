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
		AssetManager.addModel("cinder", new ModelCinder());
		AssetManager.addModel("lobber", new ModelLobber());
		AssetManager.addModel("cephignis", new ModelCephignis());
        AssetManager.addModel("afrit", new ModelAfrit());
        AssetManager.addModel("khalk", new ModelKhalk());
        AssetManager.addModel("salamander", new ModelSalamander());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}