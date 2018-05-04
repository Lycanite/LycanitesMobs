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
	private boolean summoning;

	/**
	 * Constructor
	 * @param parentGui The Beastiary GUI using this list.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiSubspeciesList(GuiBeastiary parentGui, boolean summoning, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;
		this.summoning = summoning;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		// Clear:
		this.subspeciesList.clear();

		if(!this.summoning) {
			this.creature = this.parentGui.playerExt.selectedCreature;
		}
		else {
			this.creature = this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo();
		}
		if(this.creature == null) {
			return;
		}

		this.subspeciesList.put(0, 0);
		int index = 1;
		for(int subspeciesIndex : this.creature.subspecies.keySet()) {
			if(!this.parentGui.playerExt.getBeastiary().hasKnowledgeRank(this.creature.getName(), 2)) {
				continue;
			}
			Subspecies subspecies = this.creature.subspecies.get(subspeciesIndex);
			if (subspecies != null && "rare".equals(subspecies.rarity)) {
				continue;
			}
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
		if(!this.summoning) {
			this.parentGui.playerExt.selectedSubspecies = this.subspeciesList.get(index);
		}
		else {
			this.parentGui.playerExt.getSelectedSummonSet().setSubspecies(index);
			this.parentGui.playerExt.sendSummonSetToServer((byte)this.parentGui.playerExt.selectedSummonSet);
		}
	}


	@Override
	protected boolean isSelected(int index) {
		if(!this.summoning) {
			return this.parentGui.playerExt.selectedSubspecies == this.subspeciesList.get(index);
		}
		else {
			return this.parentGui.playerExt.getSelectedSummonSet().getSubspecies() == index;
		}
	}
	

	@Override
	protected void drawBackground() {
		if(!this.summoning) {
			if(this.creature != this.parentGui.playerExt.selectedCreature) {
				this.refreshList();
			}
		}
		else {
			if(this.creature != this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo()) {
				this.refreshList();
			}
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
