package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.gui.GUIMinion;
import lycanite.lycanitesmobs.api.gui.GUIMinionSelection;
import lycanite.lycanitesmobs.api.packet.PacketGUIRequest;
import lycanite.lycanitesmobs.api.packet.PacketPlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KeyHandler {
	public Minecraft mc;
	
	public KeyBinding mountAbility = new KeyBinding("Mount Ability", Keyboard.KEY_F, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding("Mount Inventory", Keyboard.KEY_G, "Lycanites Mobs");
	public KeyBinding minionManager = new KeyBinding("Minion Manager", Keyboard.KEY_H, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding("Minion Selection", Keyboard.KEY_R, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		
		// Register Keys:
		ClientRegistry.registerKeyBinding(mountAbility);
		ClientRegistry.registerKeyBinding(mountInventory);
		ClientRegistry.registerKeyBinding(minionManager);
		ClientRegistry.registerKeyBinding(minionSelection);
	}
	
	
	// ==================================================
    //                    Handle Keys
    // ==================================================
	@SubscribeEvent
	public void onPlayerTick(TickEvent event) {
		if(event.type != TickEvent.Type.CLIENT)
			return;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.thePlayer);
		if(playerExt == null)
			return;
		byte controlStates = 0;
		
		// ========== GUI Keys ==========
		// Mount Inventory: Adds to control states.
		if(this.mountInventory.isPressed()) {
			controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
		}

		// Minion Manager: Opens GUI and sends data request packet.
		if(this.minionManager.isPressed()) {
			PacketGUIRequest packetGUIRequest = new PacketGUIRequest();
			packetGUIRequest.readGUI(GuiHandler.PlayerGuiType.MINION_CONTROLS.id);
			GUIMinion.openToPlayer(this.mc.thePlayer, playerExt.selectedSummonSet);
		}
		
		// Minion Selection: Closes If Not Holding:
		if((!Keyboard.isKeyDown(this.minionSelection.getKeyCode()) && !Mouse.isButtonDown(this.minionSelection.getKeyCode())) && this.mc.currentScreen instanceof GUIMinionSelection) {
			this.mc.thePlayer.closeScreen();
		}
		
		
		if(this.mc.inGameHasFocus) {
			// ========== HUD Controls ==========
			// Minion Selection: Opens GUI.
			if(this.minionSelection.getIsKeyPressed()) {
				GUIMinionSelection.openToPlayer(this.mc.thePlayer);
			}
			
			
			// ========== Action Controls ==========
			// Vanilla Jump: Adds to control states.
			if(this.mc.gameSettings.keyBindJump.getIsKeyPressed())
				controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;
			
			// Mount Ability: Adds to control states.
			if(this.mountAbility.getIsKeyPressed())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;
		}
		
		
		// ========== Sync Controls To Server ==========
		if(controlStates == playerExt.controlStates)
			return;
		PacketPlayerControl packet = new PacketPlayerControl();
		packet.readControlStates(controlStates);
		LycanitesMobs.packetPipeline.sendToServer(packet);
		playerExt.controlStates = controlStates;
	}
}
