package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GuiSubspeciesList extends GuiScrollingList {
	private GuiBeastiary parentGui;
	private CreatureInfo creature;
	private Map<Integer, Integer> subspeciesList = new HashMap<>();

	/**
	 * Constructor
	 * @param parentGui The Beastiary GUI using this list.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiSubspeciesList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		// Clear:
		this.subspeciesList.clear();

		this.creature = this.parentGui.playerExt.selectedCreature;
		if(this.creature == null) {
			return;
		}

		this.subspeciesList.put(0, 0);
		int index = 1;
		for(int subspeciesIndex : this.creature.subspecies.keySet()) {
			this.subspeciesList.put(index++, subspeciesIndex);
		}
	}


	@Override
	protected int getSize() {
		return this.subspeciesList.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
		this.parentGui.playerExt.selectedSubspecies = this.subspeciesList.get(index);
	}


	@Override
	protected boolean isSelected(int index) {
		return this.parentGui.playerExt.selectedSubspecies == this.subspeciesList.get(index);
	}
	

	@Override
	protected void drawBackground() {
		if(this.creature != this.parentGui.playerExt.selectedCreature) {
			this.refreshList();
		}
	}


    @Override
    protected int getContentHeight() {
        return this.getSize() * this.slotHeight;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		int subspeciesId = this.subspeciesList.get(index);
		Subspecies subspecies = this.creature.getSubspecies(subspeciesId);

		// Name:
		int nameY = boxTop + 6;
		if(subspecies == null) {
			this.parentGui.getFontRenderer().drawString("Normal", this.left + 20, nameY, 0xFFFFFF);
			return;
		}
		this.parentGui.getFontRenderer().drawString(subspecies.getTitle(), this.left + 20, nameY, 0xFFFFFF);
	}
}
