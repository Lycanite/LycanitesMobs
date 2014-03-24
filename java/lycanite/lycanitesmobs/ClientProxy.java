package lycanite.lycanitesmobs;

import java.util.Map.Entry;

import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.gui.GuiMountOverlay;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import lycanite.lycanitesmobs.api.render.RenderParticle;
import lycanite.lycanitesmobs.api.render.RenderProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
	// Render ID:
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	// ========== Register Key Bindings and Events ==========
	@Override
    public void registerEvents() {
		// Tick Handler:
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new GuiMountOverlay(Minecraft.getMinecraft()));
		
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
    }
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
		MinecraftForge.EVENT_BUS.register(new AssetManager());
		
		// ========== Add GUI Textures ==========
		String domain = LycanitesMobs.domain;
		AssetManager.addTexture("GUIInventoryCreature", domain, "textures/guis/inventory_creature.png");
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
    }
}