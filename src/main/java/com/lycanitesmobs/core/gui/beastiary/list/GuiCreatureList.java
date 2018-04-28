package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GuiCreatureList extends GuiScrollingList {
	public enum Type {
		KNOWLEDGE((byte)0), SUMMONABLE((byte)1), PET((byte)2), MOUNT((byte)3), FAMILIAR((byte)4);
		public byte id;
		Type(byte i) { id = i; }
	}

	private Type listType;
	private GuiBeastiary parentGui;
	private GuiGroupList groupList;
	private GroupInfo currentGroup;
	private Map<Integer, CreatureInfo> creatureList = new HashMap<>();
	private Map<Integer, PetEntry> petList = new HashMap<>();

	/**
	 * Constructor
	 * @param listType The type of contents to show in this list.
	 * @param parentGui The Beastiary GUI using this list.
	 * @param groupList A creature group list to restrict this list by, if null every creature is listed.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureList(Type listType, GuiBeastiary parentGui, GuiGroupList groupList, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.listType = listType;
		this.parentGui = parentGui;
		this.groupList = groupList;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		// Clear:
		this.parentGui.displayCreature = null;
		this.creatureList.clear();
		this.parentGui.displayPet = null;
		this.petList.clear();

		// Group Check:
		this.currentGroup = null;
		if(this.groupList != null) {
			this.currentGroup = this.groupList.selectedGroup;
		}
		
		int creatureIndex = 0;

		// Creature Knowledge List:
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			for(String creatureName : this.parentGui.playerExt.getBeastiary().creatureKnowledgeList.keySet()) {
				CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(creatureName.toLowerCase());
				if(this.listType == Type.SUMMONABLE && !creatureInfo.isSummonable()) {
					continue;
				}
				if (creatureInfo != null && (this.currentGroup == null || creatureInfo.group == this.currentGroup)) {
					this.creatureList.put(creatureIndex++, creatureInfo);
				}
			}
		}

		// Pet List:
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			String petType = "pet";
			if(this.listType == Type.MOUNT) {
				petType = "mount";
			}
			else if(this.listType == Type.FAMILIAR) {
				petType = "familiar";
			}
			for(PetEntry petEntry : this.parentGui.playerExt.petManager.getEntryList(petType)) {
				CreatureInfo creatureInfo = petEntry.getCreatureInfo();
				if (creatureInfo != null && (this.currentGroup == null || creatureInfo.group == this.currentGroup)) {
					this.petList.put(creatureIndex++, petEntry);
				}
			}
		}
	}


	@Override
	protected int getSize() {
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			return creatureList.size();
		}
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			return petList.size();
		}
		return 0;
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			this.parentGui.displayCreature = this.creatureList.get(index);
		}
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			this.parentGui.displayPet = this.petList.get(index);
		}
	}


	@Override
	protected boolean isSelected(int index) {
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			return this.parentGui.displayCreature != null && this.parentGui.displayCreature.equals(this.creatureList.get(index));
		}
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			return this.parentGui.displayPet != null && this.parentGui.displayPet.equals(this.petList.get(index));
		}
		return false;
	}
	

	@Override
	protected void drawBackground() {
		if(this.groupList != null) {
			if (this.currentGroup != this.groupList.selectedGroup) {
				this.refreshList();
			}
		}
	}


    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		// Knowledge Slot:
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			CreatureInfo creatureInfo = this.creatureList.get(index);
			if (creatureInfo == null) {
				return;
			}

			// Name:
			int nameY = boxTop + 6;
			if (this.listType == Type.SUMMONABLE) {
				nameY = boxTop + 2;
			}
			this.parentGui.getFontRenderer().drawString(creatureInfo.getTitle(), this.left + 20, nameY, 0xFFFFFF);

			// Level:
			if (this.listType == Type.SUMMONABLE) {
				this.parentGui.drawLevel(creatureInfo, this.left + 18, boxTop + 10);
			}

			// Icon:
			if (creatureInfo.getIcon() != null) {
				this.parentGui.drawTexturedTiled(creatureInfo.getIcon(), this.left + 2, boxTop + 2, 0, 0, 0, 16, 16, 16);
			}
		}

		// Pet Slot:
		else {
			PetEntry petEntry = this.petList.get(index);
			if (petEntry == null) {
				return;
			}

			// Name:
			int nameY = boxTop + 6;
			if (this.listType == Type.PET || this.listType == Type.MOUNT) {
				nameY = boxTop + 2;
			}
			this.parentGui.getFontRenderer().drawString(petEntry.getDisplayName(), this.left + 20, nameY, 0xFFFFFF);

			// Level:
			if (this.listType == Type.PET || this.listType == Type.MOUNT) {
				this.parentGui.drawLevel(petEntry.getCreatureInfo(), this.left + 18, boxTop + 10);
			}

			// Icon:
			if (petEntry.getCreatureInfo().getIcon() != null) {
				this.parentGui.drawTexturedTiled(petEntry.getCreatureInfo().getIcon(), this.left + 2, boxTop + 2, 0, 0, 0, 16, 16, 16);
			}
		}
	}
}
