package lycanite.lycanitesmobs;

import java.util.Map.Entry;

import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GUITabBeastiary;
import lycanite.lycanitesmobs.api.gui.GUITabMinion;
import lycanite.lycanitesmobs.api.gui.GUITabMount;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.gui.TabManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.render.RenderBlock;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import lycanite.lycanitesmobs.api.render.RenderParticle;
import lycanite.lycanitesmobs.api.render.RenderProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
	
	// Render ID:
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	// ========== Register Event Handlers ==========
	@Override
    public void registerEvents() {
		// Event Listeners:
		FMLCommonHandler.instance().bus().register(new KeyHandler(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
	}
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
		// ========== Add GUI Textures ==========
		GroupInfo group = LycanitesMobs.group;
		AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");

		// ========== Add GUI Tabs ==========
		TabManager.registerTab(new GUITabBeastiary(0));
		TabManager.registerTab(new GUITabMinion(1));
		TabManager.registerTab(new GUITabMount(2));
    }
	
	
	// ========== Register Tile Entities ==========
	@Override
	public void registerTileEntities() {
		// None
	}
	
	
	// ========== Register Renders ==========
	@Override
    public void registerRenders() {
		// Blocks:
		RenderingRegistry.registerBlockHandler(new RenderBlock());
		
		// Creatures:
		for(Entry<String, MobInfo> mobEntry : ObjectManager.mobs.entrySet())
            if(mobEntry instanceof Entity)
			    RenderingRegistry.registerEntityRenderingHandler(mobEntry.getValue().entityClass, new RenderCreature(mobEntry.getKey()));
		
		// Projectiles:
		for(Entry<String, Class> projectileEntry : ObjectManager.projectiles.entrySet())
            if(projectileEntry instanceof Entity)
                RenderingRegistry.registerEntityRenderingHandler(projectileEntry.getValue(), new RenderProjectile());
		
		// Particles:
		RenderingRegistry.registerEntityRenderingHandler(EntityParticle.class, new RenderParticle());
		
		// Special Entites:
		RenderingRegistry.registerEntityRenderingHandler(EntityPortal.class, new RenderProjectile());
    }
	
	// ========== Get Client Player Entity ==========
	@Override
    public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}