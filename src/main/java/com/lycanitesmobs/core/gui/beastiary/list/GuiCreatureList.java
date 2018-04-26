package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GuiCreatureList extends GuiScrollingList {
	public CreatureInfo selectedCreature;

	private GuiBeastiary parentGui;
	private GuiGroupList groupList;
	private GroupInfo currentGroup;
	private Map<Integer, CreatureInfo> creatureList = new HashMap<>();

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureList(GuiBeastiary parentGui, GuiGroupList groupList, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;
		this.groupList = groupList;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		this.selectedCreature = null;
		this.creatureList.clear();

		if(this.groupList == null) {
			return;
		}
		this.currentGroup = this.groupList.selectedGroup;
		
		int creatureIndex = 0;
		for(String minionName : this.parentGui.playerExt.getBeastiary().creatureKnowledgeList.keySet()) {
			CreatureInfo mobInfo = CreatureManager.getInstance().getCreature(minionName.toLowerCase());
			if(mobInfo != null && mobInfo.group == this.currentGroup) {
				this.creatureList.put(creatureIndex++, mobInfo);
			}
		}
	}


	@Override
	protected int getSize() {
		return creatureList.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedCreature = this.creatureList.get(index);
	}


	@Override
	protected boolean isSelected(int index) {
		return this.selectedCreature != null && this.selectedCreature.equals(this.creatureList.get(index));
	}
	

	@Override
	protected void drawBackground() {
		if(this.groupList == null) {
			return;
		}

		if(this.currentGroup != this.groupList.selectedGroup) {
			this.refreshList();
		}
	}


    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		CreatureInfo creatureInfo = this.creatureList.get(index);
		if(creatureInfo == null) return;
		this.parentGui.getFontRenderer().drawString(creatureInfo.getTitle(), this.left + 20 , boxTop + 4, 0xFFFFFF);
		if(creatureInfo.getIcon() != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(creatureInfo.getIcon());
			this.parentGui.drawTexturedModalRect(this.left + 2, boxTop, 0, 0, 16, 16, 16);
		}
	}
}
