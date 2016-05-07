package lycanite.lycanitesmobs.mountainmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.mountainmobs.model.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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
		AssetManager.addModel("beholder", new ModelBeholder());
		AssetManager.addModel("barghest", new ModelBarghest());
	}
}