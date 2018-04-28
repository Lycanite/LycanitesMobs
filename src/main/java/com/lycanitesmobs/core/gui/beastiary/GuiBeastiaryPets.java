package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiGroupList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiaryPets extends GuiBeastiary {
	public GuiCreatureList petList;
	public GuiCreatureList mountList;
	public GuiCreatureList familiarList;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.PETS.id, 0, 0);
		}
	}


	public GuiBeastiaryPets(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		return "Pets";
	}


	@Override
	public void initControls() {
		super.initControls();

		int petListHeight = Math.round((float)this.colLeftHeight * 0.4F);
		int petListY = this.colLeftY;
		this.petList = new GuiCreatureList(GuiCreatureList.Type.PET, this, null, this.colLeftWidth, petListHeight, petListY, petListY + petListHeight, this.colLeftX);

		int mountListHeight = Math.round((float)this.colLeftHeight * 0.4F);
		int mountListY = petListY + petListHeight;
		this.mountList = new GuiCreatureList(GuiCreatureList.Type.MOUNT, this, null, this.colLeftWidth, mountListHeight, mountListY, mountListY + mountListHeight, this.colLeftX);

		int familiarListHeight = Math.round((float)this.colLeftHeight * 0.2F);
		int familiarListY = mountListY + mountListHeight;
		this.familiarList = new GuiCreatureList(GuiCreatureList.Type.FAMILIAR, this, null, this.colLeftWidth, familiarListHeight, familiarListY,familiarListY + familiarListHeight, this.colLeftX);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);

		this.petList.drawScreen(mouseX, mouseY, partialTicks);
		this.mountList.drawScreen(mouseX, mouseY, partialTicks);
		this.familiarList.drawScreen(mouseX, mouseY, partialTicks);
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int marginX = 0;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 20;
		int width = this.colRightWidth - marginX;

		// Creature Display:
		if(this.displayPet != null) {
			// Model:
			this.renderCreature(this.displayPet.getCreatureInfo(), this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + 100, mouseX, mouseY, partialTicks);

			// Level:
			String text = I18n.translateToLocal("creature.stat.cost") + ": ";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawLevel(this.displayPet.getCreatureInfo(), nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Element:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("creature.stat.element") + ": ";
			text += this.displayPet.getCreatureInfo().element != null ? this.displayPet.getCreatureInfo().element.getTitle() : "None";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Summary:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = this.displayPet.getCreatureInfo().getDescription();
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}

		// Base Display:
		else {
			String text = I18n.translateToLocal("gui.beastiary.selectapet");
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {


		super.actionPerformed(guiButton);
	}
}
