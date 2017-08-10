package com.lycanitesmobs.swampmobs;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.swampmobs.model.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.swampmobs.model.*;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("ghoulzombie", new ModelGhoulZombie());
		AssetManager.addModel("dweller", new ModelDweller());
		AssetManager.addModel("ettin", new ModelEttin());
		AssetManager.addModel("lurker", new ModelLurker());
		AssetManager.addModel("eyewig", new ModelEyewig());
		AssetManager.addModel("aspid", new ModelAspid());
		AssetManager.addModel("remobra", new ModelRemobra());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}