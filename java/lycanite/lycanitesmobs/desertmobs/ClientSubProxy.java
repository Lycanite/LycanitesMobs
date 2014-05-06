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