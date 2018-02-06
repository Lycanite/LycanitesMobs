package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GUISummoningPedestalList extends GuiScrollingList {
    GUIBaseManager parentGUI;
	Map<Integer, String> minionList = new HashMap<>();

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUISummoningPedestalList(GUIBaseManager parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 28);
		this.parentGUI = parentGUI;
		this.minionList = playerExt.getBeastiary().getSummonableList();
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getSize() {
		return minionList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectMinion(this.minionList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedMinion() != null && this.parentGUI.getSelectedMinion().equals(this.minionList.get(index));
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		String mobName = this.minionList.get(index);
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(mobName);

		int boxLeft = this.left;
		int levelBarWidth = 9;
		int levelBarHeight = 9;
		int levelBarX = boxLeft + 20;
		int levelBarY = boxTop + boxBottom - levelBarHeight - 4;
		int levelBarU = 256 - (levelBarWidth * 2);
		int levelBarV = 256 - levelBarHeight;
		int level = creatureInfo.summonCost;

		// Summon Level:
		Minecraft.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
		for(int currentLevel = 0; currentLevel < level; currentLevel++) {
			this.parentGUI.drawTexturedModalRect(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight);
		}

		this.parentGUI.getFontRenderer().drawString(creatureInfo.getTitle(), this.left + 20 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(creatureInfo.getIcon());
		this.parentGUI.drawTexturedModalRect(this.left + 2, boxTop + 4, 0, 0, 16, 16, 16);
	}
}
