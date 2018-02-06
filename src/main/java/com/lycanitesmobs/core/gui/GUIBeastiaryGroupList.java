package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GUIBeastiaryGroupList extends GuiScrollingList {
	GUIBeastiary parentGUI;
	Map<Integer, GroupInfo> groupList = new HashMap<Integer, GroupInfo>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiaryGroupList(GUIBeastiary parentGUI, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 16);
		this.parentGUI = parentGUI;
		this.updateList();
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	public void updateList() {
		this.groupList = new HashMap<>();
		
		int groupIndex = 0;
		for(GroupInfo group : CreatureManager.getInstance().loadedGroups) {
			if(parentGUI.playerExt.beastiary.hasCreatureFromGroup(group)) {
				this.groupList.put(groupIndex++, group);
			}
		}
	}
	
	@Override
	protected int getSize() {
		return groupList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectGroup(this.groupList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedGroup() != null && this.parentGUI.getSelectedGroup().equals(this.groupList.get(index));
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		GroupInfo group = this.groupList.get(index);
		if(group == null) return;
		this.parentGUI.getFontRenderer().drawString(I18n.translateToLocal(group.filename + ".name"), this.left + 2 , boxTop + 4, 0xFFFFFF);
	}
}
