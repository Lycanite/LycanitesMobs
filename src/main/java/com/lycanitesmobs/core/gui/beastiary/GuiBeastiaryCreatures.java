package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiGroupList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiaryCreatures extends GuiBeastiary {
	public GuiGroupList groupList;
	public GuiCreatureList creatureList;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.CREATURES.id, 0, 0);
		}
	}


	public GuiBeastiaryCreatures(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		if(this.creatureList != null && this.creatureList.selectedCreature != null) {
			return this.creatureList.selectedCreature.getTitle();
		}
		if(this.groupList != null && this.groupList.selectedGroup != null) {
			return I18n.translateToLocal(this.groupList.selectedGroup.filename + ".name"); //TODO Add getTitle() to GroupInfo
		}
		return "Creatures";
	}


	@Override
	public void initControls() {
		super.initControls();

		this.groupList = new GuiGroupList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);
		int creatureListheight = this.colRightHeight;
		this.creatureList = new GuiCreatureList(this, this.groupList, this.getScaledX(240F / 1920F), creatureListheight, this.colRightY + 20,this.colRightY + creatureListheight, this.colRightX);
	}


	@Override
	public void drawBackground(int x, int y, float partialTicks) {
		super.drawBackground(x, y, partialTicks);
	}


	@Override
	protected void updateControls(int x, int y, float partialTicks) {
		this.groupList.drawScreen(x, y, partialTicks);
		if(this.groupList.selectedGroup != null) {
			this.creatureList.drawScreen(x, y, partialTicks);
		}
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);
		int marginX = this.getScaledX(240F / 1920F) + 8;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 20;
		int width = this.colRightWidth - marginX;

		// Creature Display:
		if(this.creatureList.selectedCreature != null) {
			// Model:
			this.renderCreature(this.creatureList.selectedCreature, this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + 100, mouseX, mouseY, partialTicks);

			// Level:
			String text = I18n.translateToLocal("creature.stat.cost") + ": ";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
			this.drawLevel(this.creatureList.selectedCreature, nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Element:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("creature.stat.element") + ": ";
			text += this.creatureList.selectedCreature.element != null ? this.creatureList.selectedCreature.element.getTitle() : "None";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Summary:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = this.creatureList.selectedCreature.getDescription();
			this.getFontRenderer().drawSplitString(text, nextX + 1,  nextY+ 1, width, 0x444444);
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);
		}

		// Group Display:
		else if(this.groupList.selectedGroup != null) {
			// Description:
			String text = I18n.translateToLocal(this.groupList.selectedGroup.filename + ".description");
			this.getFontRenderer().drawSplitString(text, nextX + 1,  nextY+ 1, width, 0x444444);
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);

			// Descovered:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("beastiary.creatures.descovered") + ": ";
			text += this.playerExt.getBeastiary().getCreaturesDescovered(this.groupList.selectedGroup);
			text += "/" + ObjectManager.entityLists.get(this.groupList.selectedGroup.filename).IDtoClassMapping.size();
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
		}

		// Base Display:
		else {
			String text = I18n.translateToLocal("gui.beastiary.selectacreature");
			this.getFontRenderer().drawSplitString(text, nextX + 1,  nextY+ 1, width, 0x444444);
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);
		}
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		super.actionPerformed(guiButton);
	}
}
