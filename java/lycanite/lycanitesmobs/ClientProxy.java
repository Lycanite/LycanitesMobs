package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.entity.EntityHitArea;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GUITabMain;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.gui.TabManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.render.RenderRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
	
	// Render ID:
	//public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
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
        ObjectManager.RegisterModels();

		// ========== Add GUI Textures ==========
		GroupInfo group = LycanitesMobs.group;
		AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");
		AssetManager.addTexture("GUILMMainMenu", group, "textures/guis/lmmainmenu.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIPet", group, "textures/guis/pet.png");
		AssetManager.addTexture("GUIMount", group, "textures/guis/mount.png");
        AssetManager.addTexture("GUIFamiliar", group, "textures/guis/familiar.png");
        AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");
        AssetManager.addTexture("GUIMinionLg", group, "textures/guis/minion_lg.png");

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
    public void registerRenders(GroupInfo groupInfo) {

        // Blocks:
        //RenderingRegistry.registerBlockHandler(new RenderBlock());

        // Item:
        //MinecraftForgeClient.registerItemRenderer(ObjectManager.getItem("mobtoken"), new RenderItemMobToken());

		// Special Entites:
        groupInfo.specialClasses.add(EntityHitArea.class);
        groupInfo.specialClasses.add(EntityFear.class);
        groupInfo.projectileClasses.add(EntityPortal.class);

        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
    }
	
	// ========== Get Client Player Entity ==========
	@Override
    public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}