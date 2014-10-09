package lycanite.lycanitesmobs.shadowmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.shadowmobs.model.ModelGeonach;
import lycanite.lycanitesmobs.shadowmobs.model.ModelJabberwock;
import lycanite.lycanitesmobs.shadowmobs.model.ModelTroll;
import lycanite.lycanitesmobs.shadowmobs.model.ModelYale;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("jabberwock", new ModelJabberwock());
		AssetManager.addModel("troll", new ModelTroll());
		AssetManager.addModel("yale", new ModelYale());
		AssetManager.addModel("geonach", new ModelGeonach());
	}
}