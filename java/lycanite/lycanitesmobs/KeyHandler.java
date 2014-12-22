package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.gui.*;
import lycanite.lycanitesmobs.api.network.MessageGUIRequest;
import lycanite.lycanitesmobs.api.network.MessagePlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class KeyHandler {
	public static KeyHandler instance;
	public Minecraft mc;
	
	public boolean inventoryOpen = false;
	
	public KeyBinding mountAbility = new KeyBinding("Mount Ability", Keyboard.KEY_F, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding("Mount Inventory", Keyboard.KEY_H, "Lycanites Mobs");
	public KeyBinding beastiary = new KeyBinding("Beastiary", Keyboard.KEY_B, "Lycanites Mobs");
    public KeyBinding familiarManager = new KeyBinding("Familiar Manager", Keyboard.KEY_G, "Lycanites Mobs");
	public KeyBinding minionManager = new KeyBinding("Minion Manager", Keyboard.KEY_H, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding("Minion Selection", Keyboard.KEY_J, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		instance = this;
		
		// Register Keys:
		ClientRegistry.registerKeyBinding(mountAbility);
		ClientRegistry.registerKeyBinding(mountInventory);
		ClientRegistry.registerKeyBinding(beastiary);
        ClientRegistry.registerKeyBinding(familiarManager);
		ClientRegistry.registerKeyBinding(minionManager);
		ClientRegistry.registerKeyBinding(minionSelection);
	}
	
	
	// ==================================================
    //                    Handle Keys
    // ==================================================
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent event) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.thePlayer);
		if(playerExt == null)
			return;
		byte controlStates = 0;
		
		// ========== GUI Keys ==========
		// Player Inventory: Adds extra buttons to the GUI.
		if(!this.inventoryOpen && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class) {
			TabManager.addTabsToInventory((GuiContainer)mc.currentScreen);
			this.inventoryOpen = true;
		}
		if(this.inventoryOpen && (mc.currentScreen == null || mc.currentScreen.getClass() != GuiInventory.class)) {
			this.inventoryOpen = false;
		}
		
		// Mount Inventory: Adds to control states.
		if(this.mountInventory.isPressed()) {
			controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
		}

		// Beastiary: Opens GUI and sends data request packet.
		if(this.beastiary.isPressed()) {
			//MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.BEASTIARY.id);
			GUIBeastiary.openToPlayer(this.mc.thePlayer);
		}

        // Familiar Manager: Opens GUI and sends data request packet.
        if(this.familiarManager.isPressed()) {
            MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id);
            LycanitesMobs.packetHandler.sendToServer(message);
            GUIFamiliar.openToPlayer(this.mc.thePlayer);
        }

		// Minion Manager: Opens GUI and sends data request packet.
		if(this.minionManager.isPressed()) {
			//MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.MINION_MANAGER.id);
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
		MessagePlayerControl message = new MessagePlayerControl(controlStates);
		LycanitesMobs.packetHandler.sendToServer(message);
		playerExt.controlStates = controlStates;
	}
}
