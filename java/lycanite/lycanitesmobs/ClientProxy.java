package lycanite.lycanitesmobs;

import java.util.Map.Entry;

import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import lycanite.lycanitesmobs.api.render.RenderParticle;
import lycanite.lycanitesmobs.api.render.RenderProjectile;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	// Render ID:
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	// ========== Register Key Bindings and Events ==========
	@Override
    public void registerEvents() {
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new KeyHandler(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
	}
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
		// ========== Add GUI Textures ==========
		String domain = LycanitesMobs.domain;
		AssetManager.addTexture("GUIInventoryCreature", domain, "textures/guis/inventory_creature.png");
		AssetManager.addTexture("GUIMinion", domain, "textures/guis/minion.png");
		AssetManager.addTexture("GUIBeastiary", domain, "textures/guis/beastiary.png");
    }
	
	
	// ========== Register Tile Entities ==========
	@Override
	public void registerTileEntities() {
		// None
	}
	
	
	// ========== Register Renders ==========
	@Override
    public void registerRenders() {
		// Creatures:
		for(Entry<String, MobInfo> mobEntry : ObjectManager.mobs.entrySet())
			RenderingRegistry.registerEntityRenderingHandler(mobEntry.getValue().entityClass, new RenderCreature(mobEntry.getKey()));
		
		// Projectiles:
		for(Entry<String, Class> projectileEntry : ObjectManager.projectiles.entrySet())
			RenderingRegistry.registerEntityRenderingHandler(projectileEntry.getValue(), new RenderProjectile());
		
		// Particles:
		RenderingRegistry.registerEntityRenderingHandler(EntityParticle.class, new RenderParticle());
		
		// Special Entites:
		RenderingRegistry.registerEntityRenderingHandler(EntityPortal.class, new RenderProjectile());
    }
}