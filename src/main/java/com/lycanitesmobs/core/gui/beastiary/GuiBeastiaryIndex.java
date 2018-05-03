package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GuiBeastiaryIndex extends GuiBeastiary {



	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.INDEX.id, 0, 0);
		}
	}


	@Override
	public String getTitle() {
		return I18n.translateToLocal("gui.beastiary.index.title");
	}


	public GuiBeastiaryIndex(EntityPlayer player) {
		super(player);
	}


	@Override
	protected void initControls() {
		super.initControls();

		int menuWidth = this.colRightWidth;

		int buttonCount = 2;
		int buttonPadding = 2;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - buttonPadding;
		int buttonWidthPadded = buttonWidth + buttonPadding;
		int buttonHeight = 20;
		int buttonX = this.colRightX + buttonPadding;
		int buttonY = this.colRightY + this.colRightHeight - buttonHeight;
		GuiButton button;

		// Links:
		button = new GuiButton(100, buttonX, buttonY, buttonWidth, buttonHeight, "Website");
		this.buttonList.add(button);
		button = new GuiButton(101, buttonX + buttonWidthPadded, buttonY, buttonWidth, buttonHeight, "Patreon");
		this.buttonList.add(button);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		String info = I18n.translateToLocal("gui.beastiary.index.description");
		this.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
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
			super.actionPerformed(guiButton);
		}

		super.actionPerformed(guiButton);
	}
}
