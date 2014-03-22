package lycanite.lycanitesmobs;

import java.io.File;

public class CommonProxy {
	
	// ========== Register Key Bindings ==========
    public void registerEvents() {
		// Tick Handler:
		//TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
    	
		// Event Listeners:
    	//MinecraftForge.EVENT_BUS.register(new EventListener());
	}
	
	
	// ========== Register Tile Entities ==========
	public void registerTileEntities() {
		// None
	}
    
	
    // ========== Get Minecraft Directory ==========
    public File getMinecraftDir() {
    	return new File(".");
    }
	
	
	// ========== Client Only ==========
    public void registerAssets() {}
    public void registerRenders() {}
}
