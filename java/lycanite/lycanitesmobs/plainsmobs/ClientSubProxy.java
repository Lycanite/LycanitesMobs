package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.plainsmobs.model.ModelKobold;
import lycanite.lycanitesmobs.plainsmobs.model.ModelMaka;
import lycanite.lycanitesmobs.plainsmobs.model.ModelMakaAlpha;
import lycanite.lycanitesmobs.plainsmobs.model.ModelRoc;
import lycanite.lycanitesmobs.plainsmobs.model.ModelVentoraptor;
import lycanite.lycanitesmobs.plainsmobs.model.ModelZoataur;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("kobold", new ModelKobold());
		AssetManager.addModel("ventoraptor", new ModelVentoraptor());
		AssetManager.addModel("maka", new ModelMaka());
		AssetManager.addModel("makaalpha", new ModelMakaAlpha());
		AssetManager.addModel("zoataur", new ModelZoataur());
		AssetManager.addModel("roc", new ModelRoc());
	}
}