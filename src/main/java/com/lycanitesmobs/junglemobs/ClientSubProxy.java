package com.lycanitesmobs.junglemobs;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.junglemobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.junglemobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("geken", new ModelGeken());
		AssetManager.addModel("uvaraptor", new ModelUvaraptor());
		AssetManager.addModel("concapede", new ModelConcapedeHead());
		AssetManager.addModel("concapedesegment", new ModelConcapedeSegment());
		AssetManager.addModel("tarantula", new ModelTarantula());
		AssetManager.addModel("conba", new ModelConba());
		AssetManager.addModel("vespid", new ModelVespid());
		AssetManager.addModel("vespidqueen", new ModelVespidQueen());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}