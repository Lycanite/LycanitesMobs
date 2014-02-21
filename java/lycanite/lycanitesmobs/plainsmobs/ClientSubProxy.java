package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.plainsmobs.model.ModelKobold;
import lycanite.lycanitesmobs.plainsmobs.model.ModelMaka;
import lycanite.lycanitesmobs.plainsmobs.model.ModelMakaAlpha;
import lycanite.lycanitesmobs.plainsmobs.model.ModelVentoraptor;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Kobold", new ModelKobold());
		AssetManager.addModel("Ventoraptor", new ModelVentoraptor());
		AssetManager.addModel("Maka", new ModelMaka());
		AssetManager.addModel("MakaAlpha", new ModelMakaAlpha());
	}
}