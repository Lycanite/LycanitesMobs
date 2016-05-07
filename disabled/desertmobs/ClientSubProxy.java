package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.desertmobs.model.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("cryptzombie", new ModelCryptZombie());
		AssetManager.addModel("crusk", new ModelCrusk());
		AssetManager.addModel("clink", new ModelClink());
		AssetManager.addModel("joust", new ModelJoust());
		AssetManager.addModel("joustalpha", new ModelJoustAlpha());
		AssetManager.addModel("erepede", new ModelErepede());
		AssetManager.addModel("gorgomite", new ModelGorgomite());
		AssetManager.addModel("manticore", new ModelManticore());
	}
}