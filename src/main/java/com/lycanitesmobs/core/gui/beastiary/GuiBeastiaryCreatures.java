package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiGroupList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiSubspeciesList;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiaryCreatures extends GuiBeastiary {
	public GuiGroupList groupList;
	public GuiCreatureList creatureList;
	public GuiSubspeciesList subspeciesList;

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
		if(this.creatureList != null && this.playerExt.selectedCreature != null) {
			return this.playerExt.selectedCreature.getTitle();
		}
		if(this.groupList != null && this.playerExt.selectedGroup != null) {
			return I18n.translateToLocal(this.playerExt.selectedGroup.filename + ".name"); //TODO Add getTitle() to GroupInfo
		}
		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			I18n.translateToLocal("gui.beastiary.creatures.empty.title");
		}
		return I18n.translateToLocal("gui.beastiary.creatures");
	}


	@Override
	public void initControls() {
		super.initControls();

		this.groupList = new GuiGroupList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);

		int creatureListHeight = Math.round((float)this.colRightHeight * 0.6f);
		int creatureListY = this.colRightY + 20;
		this.creatureList = new GuiCreatureList(GuiCreatureList.Type.KNOWLEDGE, this, this.groupList, this.getScaledX(240F / 1920F), creatureListHeight, creatureListY,creatureListY + creatureListHeight, this.colRightX);

		int subspeciesListHeight = Math.round((float)this.colRightHeight * 0.3f);
		int subspeciesListY = creatureListY + 8 + creatureListHeight;
		this.subspeciesList = new GuiSubspeciesList(this, false, this.getScaledX(240F / 1920F), subspeciesListHeight, subspeciesListY,subspeciesListY + subspeciesListHeight, this.colRightX);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);

		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			return;
		}

		this.groupList.drawScreen(mouseX, mouseY, partialTicks);
		if(this.playerExt.selectedGroup != null) {
			this.creatureList.drawScreen(mouseX, mouseY, partialTicks);
			this.subspeciesList.drawScreen(mouseX, mouseY, partialTicks);
		}
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int marginX = this.getScaledX(240F / 1920F) + 8;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 20;
		int width = this.colRightWidth - marginX;

		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			String text = I18n.translateToLocal("gui.beastiary.creatures.empty.info");
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
			return;
		}

		// Creature Display:
		if(this.playerExt.selectedCreature != null) {
			// Model:
			this.renderCreature(this.playerExt.selectedCreature, this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + 100, mouseX, mouseY, partialTicks);

			// Element:
			String text = "\u00A7l" + I18n.translateToLocal("creature.stat.element") + ": " + "\u00A7r";
			text += this.playerExt.selectedCreature.elements != null ? this.playerExt.selectedCreature.getElementNames() : "None";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Subspecies:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + I18n.translateToLocal("creature.stat.subspecies") + ": " + "\u00A7r";
			boolean firstSubspecies = true;
			for(Subspecies subspecies : this.playerExt.selectedCreature.subspecies.values()) {
				if(!firstSubspecies) {
					text += ", ";
				}
				firstSubspecies = false;
				text += subspecies.getTitle();
			}
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Level:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + I18n.translateToLocal("creature.stat.cost") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawLevel(this.playerExt.selectedCreature, AssetManager.getTexture("GUIPetLevel"),nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Summary:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = this.playerExt.selectedCreature.getDescription();
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}

		// Group Display:
		else if(this.playerExt.selectedGroup != null) {
			// Description:
			String text = I18n.translateToLocal(this.playerExt.selectedGroup.filename + ".description");
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);

			// Descovered:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("gui.beastiary.creatures.descovered") + ": ";
			text += this.playerExt.getBeastiary().getCreaturesDescovered(this.playerExt.selectedGroup);
			text += "/" + ObjectManager.entityLists.get(this.playerExt.selectedGroup.filename).IDtoClassMapping.size();
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
		}

		// Base Display:
		else {
			String text = I18n.translateToLocal("gui.beastiary.creatures.select");
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
		}
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		super.actionPerformed(guiButton);
	}
}
