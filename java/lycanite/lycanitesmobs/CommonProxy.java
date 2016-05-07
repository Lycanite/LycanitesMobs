package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;

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
    public void registerRenders(GroupInfo groupInfo) {}
    public EntityPlayer getClientPlayer() { return null; }
}
