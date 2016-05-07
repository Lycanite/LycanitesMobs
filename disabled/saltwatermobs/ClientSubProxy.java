package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.saltwatermobs.model.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("lacedon", new ModelLacedon());
		AssetManager.addModel("skylus", new ModelSkylus());
        AssetManager.addModel("ika", new ModelIka());
        AssetManager.addModel("abtu", new ModelAbtu());
		AssetManager.addModel("raiko", new ModelRaiko());
	}
}