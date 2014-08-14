package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.arcticmobs.model.*;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("reiver", new ModelReiver());
		AssetManager.addModel("frostweaver", new ModelFrostweaver());
		AssetManager.addModel("yeti", new ModelYeti());
		AssetManager.addModel("wendigo", new ModelWendigo());
        AssetManager.addModel("arix", new ModelArix());
	}
}