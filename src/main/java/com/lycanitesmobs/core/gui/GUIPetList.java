package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.ArrayList;
import java.util.List;

public class GUIPetList extends GuiScrollingList {
	GUIBaseManager parentGUI;
	List<PetEntry> petList = new ArrayList<>();

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIPetList(GUIBaseManager parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 28);
		this.parentGUI = parentGUI;
		this.petList = playerExt.petManager.getEntryList(parentGUI.type);
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getSize() {
		return this.petList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectPet(this.petList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedPet() != null && this.parentGUI.getSelectedPet().equals(this.petList.get(index));
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		PetEntry petEntry = this.petList.get(index);
		CreatureInfo creatureInfo = petEntry.getCreatureInfo();
		if(creatureInfo == null)
			return;

		int boxLeft = this.left;
		int levelBarWidth = 9;
		int levelBarHeight = 9;
		int levelBarX = boxLeft + 20;
		int levelBarY = boxTop + boxBottom - levelBarHeight - 4;
		int levelBarU = 256 - levelBarWidth;
		int levelBarV = 256 - levelBarHeight;
		int level = creatureInfo.summonCost;

		// Spawned Entry:
		if(petEntry.spawningActive) {
            if(this.isSelected(index))
                super.drawGradientRect(boxLeft, boxTop, boxRight, boxBottom, 0x99FFEE, 0x99FFEE);
            super.drawGradientRect(boxLeft, boxTop, boxRight, boxBottom, 0x448877, 0x448877);

			// Active Level Texture:
			levelBarU -= levelBarWidth;
		}

		// Summon Level:
		Minecraft.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
		for(int currentLevel = 0; currentLevel < level; currentLevel++) {
			this.parentGUI.drawTexturedModalRect(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight);
		}

		this.parentGUI.getFontRenderer().drawString(petEntry.getDisplayName(), boxLeft + 20 , boxTop + 2, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(creatureInfo.getIcon());
		this.parentGUI.drawTexturedModalRect(this.left + 2, boxTop + 4, 0, 0, 16, 16, 16);
	}
}
