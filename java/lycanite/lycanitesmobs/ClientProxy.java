package lycanite.lycanitesmobs;

import java.util.Map.Entry;

import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.*;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.MinecraftForgeClient;
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
		AssetManager.addTexture("GUILMMainMenu", group, "textures/guis/lmmainmenu.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIPet", group, "textures/guis/pet.png");
		AssetManager.addTexture("GUIMount", group, "textures/guis/mount.png");
        AssetManager.addTexture("GUIFamiliar", group, "textures/guis/familiar.png");
        AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");

		// ========== Add GUI Tabs ==========
		TabManager.registerTab(new GUITabMain(0));
		//TabManager.registerTab(new GUITabBeastiary(1));
		//TabManager.registerTab(new GUITabMinion(2));
		//TabManager.registerTab(new GUITabMount(3));
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

        // Item:
        MinecraftForgeClient.registerItemRenderer(ObjectManager.getItem("mobtoken"), new RenderItemMobToken());
		
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
		RenderingRegistry.registerEntityRenderingHandler(EntityFear.class, new RenderNone());
    }
	
	// ========== Get Client Player Entity ==========
	@Override
    public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}