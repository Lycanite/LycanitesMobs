package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.gui.GUIMinion;
import lycanite.lycanitesmobs.api.gui.GUIMinionSelection;
import lycanite.lycanitesmobs.api.packet.PacketGUIRequest;
import lycanite.lycanitesmobs.api.packet.PacketPlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler {
	public Minecraft mc;
	public EntityClientPlayerMP player;
	
	public KeyBinding mountAbility = new KeyBinding("Mount Ability", Keyboard.KEY_F, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding("Mount Inventory", Keyboard.KEY_G, "Lycanites Mobs");
	public KeyBinding minionManager = new KeyBinding("Minion Manager", Keyboard.KEY_H, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding("Minion Selection", Keyboard.KEY_R, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		this.player = mc.thePlayer;
		
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
	public void onKeyInput(KeyInputEvent event) {
		if(this.player == null)
			return;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.player);
		if(playerExt == null)
			return;
		byte controlStates = 0;
		
		// ========== GUI Keys ==========
		// Mount Inventory: Adds to control states.
		if(mountInventory.isPressed()) {
			controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
		}

		// Minion Manager: Opens GUI and sends data request packet.
		if(minionManager.isPressed()) {
			PacketGUIRequest packetGUIRequest = new PacketGUIRequest();
			packetGUIRequest.readGUI(GuiHandler.PlayerGuiType.MINION_CONTROLS.id);
			GUIMinion.openToPlayer(player, playerExt.selectedSummonSet);
		}
		
		
		if(mc.currentScreen == null) {
			// ========== HUD Controls ==========
			// Minion Selection: Opens GUI, closes if not pressed.
			if(minionSelection.getIsKeyPressed()) {
				GUIMinionSelection.openToPlayer(player);
			}
			else if(Minecraft.getMinecraft().currentScreen instanceof GUIMinionSelection) {
	    		player.closeScreen();
			}
			
			
			// ========== Action Controls ==========
			// Vanilla Jump: Adds to control states.
			if(this.mc.gameSettings.keyBindJump.getIsKeyPressed())
				controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;
			
			// Mount Ability: Adds to control states.
			if(mountAbility.getIsKeyPressed())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;
		}
		
		
		// ========== Sync Controls To Server ==========
		if(controlStates == playerExt.controlStates)
			return;
		PacketPlayerControl packet = new PacketPlayerControl();
		packet.readControlStates(controlStates);
		LycanitesMobs.packetPipeline.sendToServer(packet);
	}
}
