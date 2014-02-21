package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.swampmobs.model.ModelAspid;
import lycanite.lycanitesmobs.swampmobs.model.ModelDweller;
import lycanite.lycanitesmobs.swampmobs.model.ModelEttin;
import lycanite.lycanitesmobs.swampmobs.model.ModelEyewig;
import lycanite.lycanitesmobs.swampmobs.model.ModelGhoulZombie;
import lycanite.lycanitesmobs.swampmobs.model.ModelLurker;
import lycanite.lycanitesmobs.swampmobs.model.ModelRemobra;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("GhoulZombie", new ModelGhoulZombie());
		AssetManager.addModel("Dweller", new ModelDweller());
		AssetManager.addModel("Ettin", new ModelEttin());
		AssetManager.addModel("Lurker", new ModelLurker());
		AssetManager.addModel("Eyewig", new ModelEyewig());
		AssetManager.addModel("Aspid", new ModelAspid());
		AssetManager.addModel("Remobra", new ModelRemobra());
	}
}