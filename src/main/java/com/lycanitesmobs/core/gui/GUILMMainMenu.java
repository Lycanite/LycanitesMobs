package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GUILMMainMenu extends GUIBaseScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;

	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.getEntityWorld() != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.getEntityWorld(), GuiHandler.PlayerGuiType.LM_MAIN_MENU.id, 0, 0);
	}

	public boolean doesGuiPauseGame() {
        return false;
    }

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}


	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUILMMainMenu(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
		this.windowWidth = 176;
		this.windowHeight = 166;
		this.windowX = this.centerX - (this.windowWidth / 2);
		this.windowY = this.centerY - (this.windowHeight / 2);
		this.drawControls();
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.updateControls();
        this.drawGuiContainerForegroundLayer();
        
        // Creature List:
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.getFontRenderer().drawString(LycanitesMobs.name, this.windowX + 51, this.windowY + 6, 0xFFFFFF);
		this.getFontRenderer().drawString(LycanitesMobs.version, this.windowX + 4, this.windowY + this.windowHeight - 12, 0xFFFFFF);
	}
	
	
	// ==================================================
	//                    Background
	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUILMMainMenu"));
		this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);
	}
	
	
	// ==================================================
  	//                     Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = (this.windowWidth - 16) / 2;
        int buttonHeight = 20;
		int buttonX =this.centerX - Math.round(buttonWidth / 2);
        int buttonXLeft = this.centerX - buttonWidth - buttonSpacing;
		int buttonXRight = this.centerX + buttonSpacing;
        int buttonY = this.centerY - Math.round(buttonHeight / 2);
        GuiButton button;
        int nextY = this.windowY + buttonHeight + buttonSpacing;

		// Beastiary:
        button = new GuiButton(GuiHandler.PlayerGuiType.BEASTIARY_OLD.id, buttonXLeft, nextY, buttonWidth, buttonHeight, "Beastiary");
    	this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.BEASTIARY.id, buttonXRight, nextY, buttonWidth, buttonHeight, "New Beastiary");
		this.buttonList.add(button);

		// Pet & Mount Managers:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.PET_MANAGER.id, buttonXLeft, nextY, buttonWidth, buttonHeight, "Pets");
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.MOUNT_MANAGER.id, buttonXRight, nextY, buttonWidth, buttonHeight, "Mounts");
		this.buttonList.add(button);

		// Minion & Familiar Managers:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.MINION_MANAGER.id, buttonXLeft, nextY, buttonWidth, buttonHeight, "Minions");
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id, buttonXRight, nextY, buttonWidth, buttonHeight, "Familiars");
		this.buttonList.add(button);

		// Web Links:
		nextY += (buttonHeight + buttonSpacing) * 2; // Double space for a nice gap.
		button = new GuiButton(100, buttonXLeft, nextY, buttonWidth, buttonHeight, "Website");
		this.buttonList.add(button);
		button = new GuiButton(101, buttonXRight, nextY, buttonWidth, buttonHeight, "Patreon");
		this.buttonList.add(button);
    }

	public void updateControls() {}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			if(guiButton.id == GuiHandler.PlayerGuiType.BEASTIARY_OLD.id) {
				GUIBeastiary.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.PET_MANAGER.id) {
				GUIPetManager.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.MOUNT_MANAGER.id) {
				GUIMountManager.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.MINION_MANAGER.id) {
				GUIMinion.openToPlayer(this.player, this.playerExt.selectedSummonSet);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id) {
				GUIFamiliar.openToPlayer(this.player);
			}
			if(guiButton.id == 100) {
				try {
					this.openURI(new URI(LycanitesMobs.website));
				} catch (URISyntaxException e) {}
			}
			if(guiButton.id == 101) {
				try {
					this.openURI(new URI(LycanitesMobs.websitePatreon));
				} catch (URISyntaxException e) {}
			}
		}
		super.actionPerformed(guiButton);
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        	 this.mc.player.closeScreen();
		super.keyTyped(par1, par2);
	}


	// ==================================================
	//                     Open URI
	// ==================================================
	private void openURI(URI uri) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[]{uri});
		}
		catch (Throwable throwable) {
			LycanitesMobs.printWarning("", "Unable to open link: " + uri.toString());
		}
	}
}
