package com.lycanitesmobs;

import com.lycanitesmobs.core.gui.*;
import com.lycanitesmobs.core.gui.*;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiaryIndex;
import com.lycanitesmobs.core.network.MessagePlayerAttack;
import com.lycanitesmobs.core.network.MessagePlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyHandler {
	public static KeyHandler instance;
	public Minecraft mc;
	
	public boolean inventoryOpen = false;
	
	public KeyBinding mountAbility = new KeyBinding(I18n.translateToLocal("key.mount.ability"), Keyboard.KEY_X, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding(I18n.translateToLocal("key.mount.inventory"), Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding beastiary = new KeyBinding(I18n.translateToLocal("key.beastiary"), Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding lmMainMenu = new KeyBinding(I18n.translateToLocal("key.lycanitesmobs.menu"), Keyboard.KEY_G, "Lycanites Mobs");
	public KeyBinding petManager = new KeyBinding(I18n.translateToLocal("key.pet.manager"), Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding mountManager = new KeyBinding(I18n.translateToLocal("key.mount.manager"), Keyboard.KEY_NONE, "Lycanites Mobs");
    public KeyBinding familiarManager = new KeyBinding(I18n.translateToLocal("key.familiar.manager"), Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding minionManager = new KeyBinding(I18n.translateToLocal("key.minion.manager"), Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding(I18n.translateToLocal("key.minion.select"), Keyboard.KEY_R, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		instance = this;
		
		// Register Keys:
		ClientRegistry.registerKeyBinding(mountAbility);
		ClientRegistry.registerKeyBinding(mountInventory);
		ClientRegistry.registerKeyBinding(lmMainMenu);
		ClientRegistry.registerKeyBinding(beastiary);
		ClientRegistry.registerKeyBinding(petManager);
		ClientRegistry.registerKeyBinding(mountManager);
        ClientRegistry.registerKeyBinding(familiarManager);
		ClientRegistry.registerKeyBinding(minionManager);
		ClientRegistry.registerKeyBinding(minionSelection);
	}
	
	
	// ==================================================
    //                    Handle Keys
    // ==================================================
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent event) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
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

		// LM Main Menu:
		if(this.lmMainMenu.isPressed()) {
			GUILMMainMenu.openToPlayer(this.mc.player);
		}

		// Beastiary:
		if(this.beastiary.isPressed()) {
			//GUIBeastiary.openToPlayer(this.mc.player);
			GuiBeastiaryIndex.openToPlayer(this.mc.player);
		}

		// Pet Manager:
		if(this.petManager.isPressed()) {
			GUIPetManager.openToPlayer(this.mc.player);
		}

		// Mount Manager:
		if(this.mountManager.isPressed()) {
			GUIMountManager.openToPlayer(this.mc.player);
		}

        // Familiar Manager:
        if(this.familiarManager.isPressed()) {
            GUIFamiliar.openToPlayer(this.mc.player);
        }

		// Minion Manager:
		if(this.minionManager.isPressed()) {
			GUIMinion.openToPlayer(this.mc.player, playerExt.selectedSummonSet);
		}
		
		// Minion Selection: Closes If Not Holding:
		try {
			if ((!Keyboard.isKeyDown(this.minionSelection.getKeyCode()) && !Mouse.isButtonDown(this.minionSelection.getKeyCode())) && this.mc.currentScreen instanceof GUIMinionSelection) {
				this.mc.player.closeScreen();
			}
		}
		catch(Exception e) {}
		
		
		if(this.mc.inGameHasFocus) {
			// ========== HUD Controls ==========
			// Minion Selection:
			if(this.minionSelection.isPressed()) {
				GUIMinionSelection.openToPlayer(this.mc.player);
			}
			
			
			// ========== Action Controls ==========
			// Vanilla Jump: Adds to control states.
			if(this.mc.gameSettings.keyBindJump.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;
			
			// Mount Ability: Adds to control states.
			if(this.mountAbility.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;

            // Attack Key Pressed:
            if(Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
                controlStates += ExtendedPlayer.CONTROL_ID.ATTACK.id;
            }
		}
		
		
		// ========== Sync Controls To Server ==========
		if(controlStates == playerExt.controlStates)
			return;
		MessagePlayerControl message = new MessagePlayerControl(controlStates);
		LycanitesMobs.packetHandler.sendToServer(message);
		playerExt.controlStates = controlStates;
	}


    // ==================================================
    //                   Item Use Events
    // ==================================================
    /** Player 'mouse' events, these are actually events based on attack or item use actions and are still triggered if the key binding is no longer a mouse click. **/
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
        if(this.mc.player == null || playerExt == null || this.mc.objectMouseOver == null)
            return;

        // Left (Attack):
        if(event.getButton() == 0) {
            // Disable attack for large entity reach override:
            if(!this.mc.player.isSpectator() && !this.mc.player.isRowingBoat() && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entityHit = this.mc.objectMouseOver.entityHit;
                if(playerExt.canMeleeBigEntity(entityHit)) {
                    MessagePlayerAttack message = new MessagePlayerAttack(entityHit);
                    LycanitesMobs.packetHandler.sendToServer(message);
                    //event.setCanceled(true);
                }
            }
        }
    }
}
