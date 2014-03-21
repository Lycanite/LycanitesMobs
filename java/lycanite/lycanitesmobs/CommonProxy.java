package lycanite.lycanitesmobs;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	
	// ========== Register Key Bindings ==========
    public void registerEvents() {
		// Tick Handler:
		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
    	
		// Event Listeners:
    	MinecraftForge.EVENT_BUS.register(new EventListener(Minecraft.getMinecraft()));
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
