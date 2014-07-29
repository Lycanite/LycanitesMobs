package lycanite.lycanitesmobs;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	
	// ========== Register Event Handlers ==========
    public void registerEvents() {
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
    public EntityPlayer getClientPlayer() { return null; }
}
