package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.gui.GuiButtonCreature;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiSubspeciesList;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiarySummoning extends GuiBeastiary {
	public GuiCreatureList petList;
	public GuiSubspeciesList subspeciesList;

	private int summoningSlotIdStart = 200;
	private int petCommandIdStart = 300;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.SUMMONING.id, 0, 0);
		}
	}


	public GuiBeastiarySummoning(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		if(this.playerExt.beastiary.getSummonableList().isEmpty()) {
			return I18n.translateToLocal("gui.beastiary.summoning.empty.title");
		}
		return I18n.translateToLocal("gui.beastiary.summoning");
	}


	@Override
	public void initControls() {
		super.initControls();

		int petListHeight = this.colLeftHeight;
		int petListY = this.colLeftY;
		this.petList = new GuiCreatureList(GuiCreatureList.Type.SUMMONABLE, this, null, this.colLeftWidth, petListHeight, petListY, petListY + petListHeight, this.colLeftX);

		int subspeciesListHeight = 80;
		int subspeciesListY = this.colRightY + 70;
		this.subspeciesList = new GuiSubspeciesList(this, true, 90, subspeciesListHeight, subspeciesListY, subspeciesListY + subspeciesListHeight, this.colRightX);

		int summoningSlots = this.playerExt.summonSetMax;
		int buttonSpacing = 2;
		int buttonWidth = 32;
		int buttonHeight = 32;
		int buttonX = this.colRightX + Math.round((float)this.colRightWidth / 2) - Math.round((buttonWidth + buttonSpacing) * ((float)summoningSlots / 2));
		int buttonY = this.colRightY + 10;

		// Summoning Slots:
		int tabSpacing = buttonSpacing;
		for(int i = 1; i <= summoningSlots; i++) {
			String buttonText = String.valueOf(i);
			CreatureInfo creatureInfo = this.playerExt.getSummonSet(i).getCreatureInfo();
			buttonX += tabSpacing;
			GuiButton tabButton = new GuiButtonCreature(this.summoningSlotIdStart + i, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, creatureInfo);
			this.buttonList.add(tabButton);
			if(i == this.playerExt.selectedSummonSet) {
				tabButton.enabled = false;
			}
			tabSpacing = buttonWidth + buttonSpacing;
		}

		int buttonMarginX = 10 + Math.max(Math.max(this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.actions")), this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.stance"))), this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.movement")));
		buttonWidth = 80;
		buttonHeight = 20;
		buttonX = this.colRightX + buttonMarginX;
		buttonY = this.colRightY + this.colRightHeight - ((buttonHeight + buttonSpacing) * 3);

		// Actions:
		GuiButton button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.PVP.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.pvp"));
		this.buttonList.add(button);

		// Stance:
		buttonX = this.colRightX + buttonMarginX;
		buttonY += buttonHeight + 2;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.PASSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.passive"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.DEFENSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.defensive"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.ASSIST.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.assist"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.AGGRESSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.aggressive"));
		this.buttonList.add(button);

		// Movement:
		buttonX = this.colRightX + buttonMarginX;
		buttonY += buttonHeight + 2;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.FOLLOW.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.follow"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.WANDER.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.wander"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND_ID.SIT.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.sit"));
		this.buttonList.add(button);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);

		if(this.playerExt.beastiary.getSummonableList().isEmpty()) {
			return;
		}

		this.petList.drawScreen(mouseX, mouseY, partialTicks);
		this.subspeciesList.drawScreen(mouseX, mouseY, partialTicks);

		// Update Buttons:
		for(GuiButton button : this.buttonList) {

			// Summoning Slots:
			if(button.id >= this.summoningSlotIdStart && button.id < this.petCommandIdStart) {
				button.enabled = button.id - this.summoningSlotIdStart != this.playerExt.selectedSummonSet;
				if(button instanceof GuiButtonCreature) {
					GuiButtonCreature buttonCreature = (GuiButtonCreature)button;
					buttonCreature.creatureInfo = this.playerExt.getSummonSet(button.id - this.summoningSlotIdStart).getCreatureInfo();
				}
			}

			// Pet Commands:
			else if(button.id >= this.petCommandIdStart) {
				if (this.playerExt.getSelectedSummonSet() != null) {
					button.visible = true;

					// Actions:
					if (button.id == EntityCreatureBase.PET_COMMAND_ID.PVP.id + this.petCommandIdStart) {
						if (this.playerExt.getSelectedSummonSet().getPVP()) {
							button.displayString = I18n.translateToLocal("gui.pet.pvp") + ": " + I18n.translateToLocal("common.yes");
						}
						else {
							button.displayString = I18n.translateToLocal("gui.pet.pvp") + ": " + I18n.translateToLocal("common.no");
						}
					}

					// Stance:
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.PASSIVE.id + this.petCommandIdStart) {
						button.enabled = !this.playerExt.getSelectedSummonSet().passive;
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.DEFENSIVE.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.getSelectedSummonSet().getPassive() && !this.playerExt.getSelectedSummonSet().getAssist() && !this.playerExt.getSelectedSummonSet().getAggressive());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.ASSIST.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.getSelectedSummonSet().getPassive() && this.playerExt.getSelectedSummonSet().getAssist() && !this.playerExt.getSelectedSummonSet().getAggressive());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.AGGRESSIVE.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.getSelectedSummonSet().getPassive() && this.playerExt.getSelectedSummonSet().getAggressive());
					}

					// Movement:
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.FOLLOW.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.getSelectedSummonSet().getSitting() && this.playerExt.getSelectedSummonSet().getFollowing());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.WANDER.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.getSelectedSummonSet().getSitting() && !this.playerExt.getSelectedSummonSet().getFollowing());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND_ID.SIT.id + this.petCommandIdStart) {
						button.enabled = !(this.playerExt.getSelectedSummonSet().getSitting());
					}
				}
				else {
					button.visible = false;
				}
			}
		}
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int marginX = 0;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 44;
		int width = this.colRightWidth - marginX;

		// Empty:
		if(this.playerExt.beastiary.getSummonableList().isEmpty()) {
			String text = I18n.translateToLocal("gui.beastiary.summoning.empty.info");
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
			return;
		}

		CreatureInfo selectedCreature = this.playerExt.getSelectedSummonSet().getCreatureInfo();

		// Model:
		if(selectedCreature != null) {
			this.renderCreature(selectedCreature, this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + Math.round((float) this.colRightHeight / 2), mouseX, mouseY, partialTicks);
		}

		// Player Summoning Focus:
		String text = "\u00A7l" + I18n.translateToLocal("gui.beastiary.player.focus") + ": ";
		this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
		int barX = nextX + this.getFontRenderer().getStringWidth(text);
		int focusMax = Math.round((float)this.playerExt.summonFocusMax / this.playerExt.summonFocusCharge);
		int focusAvailable = (int)Math.floor((double)this.playerExt.summonFocus / this.playerExt.summonFocusCharge);
		float focusFilling = ((float)this.playerExt.summonFocus / this.playerExt.summonFocusCharge) - focusAvailable;
		this.drawBar(AssetManager.getTexture("GUIPetSpiritEmpty"), barX, nextY, 0, 9, 9, focusMax, 10);
		this.drawBar(AssetManager.getTexture("GUIPetSpiritUsed"), barX, nextY, 0, 9, 9, focusAvailable, 10);
		if(focusFilling > 0) {
			this.drawTexture(AssetManager.getTexture("GUIPetSpiritFilling"), barX + (9 * focusAvailable), nextY, 0, focusFilling, 1, focusFilling * 9, 9);
		}

		// Creature Display:
		if(selectedCreature != null) {
			// Focus Cost:
			nextY += 4 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + I18n.translateToLocal("creature.stat.focus") + ": ";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawLevel(selectedCreature, AssetManager.getTexture("GUIPetLevel"), nextX + this.getFontRenderer().getStringWidth(text), nextY);
		}

		// Base Display:
		else {
			nextY += 4 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("gui.beastiary.summoning.select");
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}

		// Button Titles:
		int buttonHeight = 20;
		int buttonSpacing = 2;
		int buttonY = this.colRightY + this.colRightHeight - ((buttonHeight + buttonSpacing) * 3);
		this.getFontRenderer().drawString("\u00A7l" + I18n.translateToLocal("gui.pet.actions"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
		buttonY += buttonHeight + buttonSpacing;
		this.getFontRenderer().drawString("\u00A7l" + I18n.translateToLocal("gui.pet.stance"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
		buttonY += buttonHeight + buttonSpacing;
		this.getFontRenderer().drawString("\u00A7l" + I18n.translateToLocal("gui.pet.movement"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
	}


	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// Summoning Slots:
		if(button.id >= this.summoningSlotIdStart && button.id < this.petCommandIdStart) {
			this.playerExt.setSelectedSummonSet(button.id - this.summoningSlotIdStart);
		}

		SummonSet summonSet = this.playerExt.getSelectedSummonSet();
		if(summonSet != null) {

			// Pet Commands:
			if (button.id >= this.petCommandIdStart) {
				int petCommandId = button.id - this.petCommandIdStart;

				// Actions:
				if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.PVP.id) {
					summonSet.pvp = !summonSet.pvp;
				}

				// Stance:
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.PASSIVE.id) {
					summonSet.passive = true;
					summonSet.assist = false;
					summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.DEFENSIVE.id) {
					summonSet.passive = false;
					summonSet.assist = false;
					summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.ASSIST.id) {
					summonSet.passive = false;
					summonSet.assist = true;
					summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.AGGRESSIVE.id) {
					summonSet.passive = false;
					summonSet.assist = true;
					summonSet.aggressive = true;
				}

				// Movement:
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.FOLLOW.id) {
					summonSet.following = true;
					summonSet.sitting = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.WANDER.id) {
					summonSet.following = false;
					summonSet.sitting = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND_ID.SIT.id) {
					summonSet.following = false;
					summonSet.sitting = true;
				}

				this.playerExt.sendSummonSetToServer((byte) this.playerExt.selectedSummonSet);
				if (this.playerExt.selectedPet == null) {
					openToPlayer(this.player);
				}
				return;
			}
		}

		super.actionPerformed(button);
	}


	@Override
	public int getDisplaySubspecies(CreatureInfo creatureInfo) {
		return this.playerExt.getSelectedSummonSet().subspecies;
	}


	@Override
	public void playCreatureSelectSound(CreatureInfo creatureInfo) {
		this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, AssetManager.getSound(creatureInfo.getName() + "_tame"), SoundCategory.NEUTRAL, 1, 1);
	}
}
