package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.desertmobs.model.ModelClink;
import lycanite.lycanitesmobs.desertmobs.model.ModelCrusk;
import lycanite.lycanitesmobs.desertmobs.model.ModelCryptZombie;
import lycanite.lycanitesmobs.desertmobs.model.ModelErepede;
import lycanite.lycanitesmobs.desertmobs.model.ModelGorgomite;
import lycanite.lycanitesmobs.desertmobs.model.ModelJoust;
import lycanite.lycanitesmobs.desertmobs.model.ModelJoustAlpha;
import lycanite.lycanitesmobs.desertmobs.model.ModelManticore;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("CryptZombie", new ModelCryptZombie());
		AssetManager.addModel("Crusk", new ModelCrusk());
		AssetManager.addModel("Clink", new ModelClink());
		AssetManager.addModel("Joust", new ModelJoust());
		AssetManager.addModel("JoustAlpha", new ModelJoustAlpha());
		AssetManager.addModel("Erepede", new ModelErepede());
		AssetManager.addModel("Gorgomite", new ModelGorgomite());
		AssetManager.addModel("Manticore", new ModelManticore());
	}
}