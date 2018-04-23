package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

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

	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {


		super.actionPerformed(guiButton);
	}
}
