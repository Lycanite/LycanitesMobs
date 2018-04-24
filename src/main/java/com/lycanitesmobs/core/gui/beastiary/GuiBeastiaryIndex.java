package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

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


	public GuiBeastiaryIndex(EntityPlayer player) {
		super(player);
	}


	@Override
	public void drawBackground(int x, int y, float partialTicks) {
		super.drawBackground(x, y, partialTicks);
	}


	@Override
	protected void updateControls() {

	}


	@Override
	public void drawForeground(int x, int y, float partialTicks) {
		int colRightX = this.windowX + this.getScaledX(500F / 1920F);
		int colRightY = this.windowY + this.getScaledY(420F / 1080F);
		int colRightWidth = this.getScaledX(1220F / 1920F);
		int colRightCenter = colRightX + Math.round(colRightWidth / 2);

		String title = "Leave Mortal! This Is Not Ready Yet!";
		float width = this.fontRenderer.getStringWidth(title);
		this.fontRenderer.drawString(title, colRightCenter - Math.round(width / 2), colRightY, 0xFFFFFF, true);

		String info = "This will be the brand new amazing Beastiary for Lycanites Mobs with improved Pet Management, better summoning controls and more info such as items that creatures drop, where and how creatures spawn and info on elements including their buffs and debuffs!";
		this.fontRenderer.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0x444444);
		this.fontRenderer.drawSplitString(info, colRightX, colRightY + 12, colRightWidth, 0xFFFFFF);
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {


		super.actionPerformed(guiButton);
	}
}
