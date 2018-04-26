package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class GuiBeastiarySummoning extends GuiBeastiary {



	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.SUMMONING.id, 0, 0);
		}
	}


	public GuiBeastiarySummoning(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		return "Summoning";
	}


	@Override
	public void drawBackground(int x, int y, float partialTicks) {
		super.drawBackground(x, y, partialTicks);
	}


	@Override
	protected void updateControls(int x, int y, float partialTicks) {

	}


	@Override
	public void drawForeground(int x, int y, float partialTicks) {
		super.drawForeground(x, y, partialTicks);

		String info = "Minions and stuff!";
		this.fontRenderer.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0x444444);
		this.fontRenderer.drawSplitString(info, colRightX, colRightY + 12, colRightWidth, 0xFFFFFF);
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {


		super.actionPerformed(guiButton);
	}
}
