package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiaryElements extends GuiBeastiary {



	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.ELEMENTS.id, 0, 0);
		}
	}


	public GuiBeastiaryElements(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		return I18n.translateToLocal("gui.beastiary.elements");
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

		String info = I18n.translateToLocal("gui.beastiary.elements.comingsoon");
		this.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {


		super.actionPerformed(guiButton);
	}
}
