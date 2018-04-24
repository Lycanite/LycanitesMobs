package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class GuiBeastiary extends GUIBaseScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;

	public ScaledResolution scaledResolution;
	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

	/**
	 * Constructor
	 * @param player The player to create the GUI instance for.
	 */
	public GuiBeastiary(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}


	/**
	 * Returns the font renderer.
	 * @return The font renderer to use.
	 */
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}


	/**
	 * Whether this GUI should pause a single player game or not.
	 * @return True to pause the game.
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}


	/**
	 * Returns a scaled x coordinate.
	 * @param x The x float to scale where 1.0 is the entire GUI width.
	 * @return A scaled x position.
	 */
	public int getScaledX(float x) {
		if(this.scaledResolution == null) {
			this.scaledResolution = new ScaledResolution(this.mc);
		}
		return Math.round((float)scaledResolution.getScaledWidth() * x);
	}


	/**
	 * Returns a scaled y coordinate based on the scaled width with an aspect ratio applied to it.
	 * @param y The y float to scale where 1.0 is the entire GUI height.
	 * @return A scaled y position.
	 */
	public int getScaledY(float y) {
		float baseHeight = Math.round((float)this.getScaledX(y) * (1080F / 1920F));
		return Math.round(baseHeight * y);
	}


	/**
	 * Initializes this gui, called when first opening or on window resizing.
	 */
	@Override
	public void initGui() {
		this.windowWidth = this.getScaledX(0.95F);
		this.windowHeight = this.getScaledY(0.95F);
		this.halfX = this.windowWidth / 2;
		this.halfY = this.windowHeight / 2;
		this.windowX = (this.width / 2) - (this.windowWidth / 2);
		this.windowY = (this.height / 2) - (this.windowHeight / 2);
		this.centerX = this.windowX + (this.windowWidth / 2);
		this.centerY = this.windowY + (this.windowHeight / 2);

		this.buttonList.clear();
		this.initControls();
	}


	/**
	 * Draws the buttons and other controls for this GUI.
	 */
	protected void initControls() {
		int menuPadding = 6;
		int menuX = this.centerX - Math.round((float)this.windowWidth / 2) + menuPadding;
		int menuY = this.windowY + menuPadding;
		int menuWidth = this.windowWidth - (menuPadding * 2);

		int buttonCount = 6;
		int buttonPadding = 2;
		int buttonX = menuX + buttonPadding;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - (buttonPadding * 2);
		int buttonWidthPadded = buttonWidth + (buttonPadding * 2);
		int buttonHeight = 20;
		GuiButton button;

		// Top Menu:
		button = new GuiButton(GuiHandler.PlayerGuiType.BEASTIARY.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Beastiary");
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.PET_MANAGER.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Pets");
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.MINION_MANAGER.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Summoning");
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.PlayerGuiType.MOUNT_MANAGER.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Elements");
		this.buttonList.add(button);
		button = new GuiButton(100, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Website");
		this.buttonList.add(button);
		button = new GuiButton(101, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, "Patreon");
		this.buttonList.add(button);
	}


	/**
	 * Draws and updates the GUI.
	 * @param x The x position to draw from.
	 * @param y The y position to draw from.
	 * @param partialTicks Ticks for animation.
	 */
	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawBackground(x, y, partialTicks);
		this.drawForeground(x, y, partialTicks);
		this.updateControls();

		super.drawScreen(x, y, partialTicks);
	}


	/**
	 * Draws the background image.
	 * @param x The x position to draw from.
	 * @param y The y position to draw from.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawBackground(int x, int y, float partialTicks) {
		this.drawTexture(AssetManager.getTexture("GUIBeastiaryBackground"), this.windowX, this.windowY, 1, 1, this.windowWidth, this.windowHeight);
	}


	/**
	 * Updated buttons and other controls for this GUI.
	 */
	protected void updateControls() {

	}


	/**
	 * Draws foreground elements.
	 * @param x The x position to draw from.
	 * @param y The y position to draw from.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawForeground(int x, int y, float partialTicks) {

	}


	/**
	 * Called when a GUI button is interacted with.
	 * @param guiButton
	 * @throws IOException
	 */
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			if(guiButton.id == GuiHandler.PlayerGuiType.BEASTIARY.id) {
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


	/**
	 * Called when a key is pressed.
	 * @param typedChar The character typed.
	 * @param keyCode The keycode of the key pressed.
	 * @throws IOException
	 */
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			this.mc.player.closeScreen();
		}
		super.keyTyped(typedChar, keyCode);
	}


	/**
	 * Opens a URI in the users default web browser.
	 * @param uri The URI link to open.
	 */
	protected void openURI(URI uri) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, uri);
		}
		catch (Throwable throwable) {
			LycanitesMobs.printWarning("", "Unable to open link: " + uri.toString());
		}
	}
}
