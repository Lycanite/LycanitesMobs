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
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	// Render ID:
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	// ========== Register Key Bindings and Events ==========
	@Override
    public void registerEvents() {
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
		
		// Mount Jump:
		// I'm using the tick handler and player jump key instead to prevent overriding.
		//KeyBinding[] mountJumpKey = {new KeyBinding("Mount Jump", Keyboard.KEY_SPACE)};
		//boolean[] mountJumpRepeat = {false};
		//KeyBindingRegistry.registerKeyBinding(new KeyMountJump(mountJumpKey, mountJumpRepeat, "Mount Jump", "MountJump"));
		
		// Mount Ability:
		KeyBinding[] mountAbilityKey = {new KeyBinding("Mount Ability", Keyboard.KEY_F)};
		boolean[] mountAbilityRepeat = {true};
		KeyBindingRegistry.registerKeyBinding(new KeyBase(mountAbilityKey, mountAbilityRepeat, "Mount Ability", "MountAbility"));
		
		// Pet Inventory:
		KeyBinding[] petInventoryKey = {new KeyBinding("Mount Inventory", Keyboard.KEY_G)};
		boolean[] petInventoryRepeat = {true};
		KeyBindingRegistry.registerKeyBinding(new KeyBase(petInventoryKey, petInventoryRepeat, "Mount Inventory", "PetInventory"));
		
		// Minion Manager GUI:
		KeyBinding[] minionKey = {new KeyBinding("Minion Controls", Keyboard.KEY_H)};
		boolean[] minionRepeat = {true};
		KeyBindingRegistry.registerKeyBinding(new KeyBase(minionKey, minionRepeat, "Minion Controls", "MinionControls"));
		
		// Minion Select GUI:
		KeyBinding[] minionSelectKey = {new KeyBinding("Minion Selection", Keyboard.KEY_R)};
		boolean[] minionSelectRepeat = {true};
		KeyBindingRegistry.registerKeyBinding(new KeyBase(minionSelectKey, minionSelectRepeat, "Minion Selection", "MinionSelect"));
    }
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
		MinecraftForge.EVENT_BUS.register(new AssetManager());
		
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