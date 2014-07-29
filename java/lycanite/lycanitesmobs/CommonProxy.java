package lycanite.lycanitesmobs;

import java.io.File;

import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	
	// ========== Register Event Handlers ==========
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new CustomSpawner());
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
