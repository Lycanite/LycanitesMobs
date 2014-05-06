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
		AssetManager.addModel("ghoulzombie", new ModelGhoulZombie());
		AssetManager.addModel("dweller", new ModelDweller());
		AssetManager.addModel("ettin", new ModelEttin());
		AssetManager.addModel("lurker", new ModelLurker());
		AssetManager.addModel("eyewig", new ModelEyewig());
		AssetManager.addModel("aspid", new ModelAspid());
		AssetManager.addModel("remobra", new ModelRemobra());
	}
}