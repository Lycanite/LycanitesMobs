package com.lycanitesmobs.plainsmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.plainsmobs.model.*;
import com.lycanitesmobs.plainsmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerRenders(GroupInfo groupInfo) {
        // Add Models:
		AssetManager.addModel("kobold", new ModelKobold());
		AssetManager.addModel("ventoraptor", new ModelVentoraptor());
		AssetManager.addModel("maka", new ModelMaka());
		AssetManager.addModel("makaalpha", new ModelMakaAlpha());
		AssetManager.addModel("zoataur", new ModelZoataur());
		AssetManager.addModel("roc", new ModelRoc());
		AssetManager.addModel("feradon", new ModelFeradon());
		AssetManager.addModel("quillbeast", new ModelQuillbeast());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}