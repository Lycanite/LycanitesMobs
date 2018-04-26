package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GuiGroupList extends GuiScrollingList {
	public GroupInfo selectedGroup;

	private GuiBeastiary parentGui;
	private Map<Integer, GroupInfo> groupList = new HashMap<>();

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiGroupList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		this.selectedGroup = null;
		this.groupList.clear();

		int groupIndex = 0;
		for(GroupInfo group : CreatureManager.getInstance().loadedGroups) {
			if(this.parentGui.playerExt.beastiary.getCreaturesDescovered(group) > 0) {
				this.groupList.put(groupIndex++, group);
			}
		}
	}


	@Override
	protected int getSize() {
		return this.groupList.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedGroup = this.groupList.get(index);
	}


	@Override
	protected boolean isSelected(int index) {
		return this.selectedGroup != null && this.selectedGroup.equals(this.groupList.get(index));
	}
	

	@Override
	protected void drawBackground() {}


    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		GroupInfo group = this.groupList.get(index);
		if(group == null) {
			return;
		} //TODO Add getTitle() to GroupInfo
		this.parentGui.getFontRenderer().drawString(I18n.translateToLocal(group.filename + ".name"), this.left + 2 , boxTop + 4, 0xFFFFFF, true);
	}
}
