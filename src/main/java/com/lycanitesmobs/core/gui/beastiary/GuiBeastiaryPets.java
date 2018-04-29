package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureFilterList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiPetTypeList;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;

import java.io.IOException;

public class GuiBeastiaryPets extends GuiBeastiary {
	public GuiCreatureFilterList petTypeList;
	public GuiCreatureList petList;
	private int petCommandIdStart = 200;
	private int releaseConfirmId = 300;
	private int releaseCancelId = 301;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.PETS.id, 0, 0);
		}
	}


	public GuiBeastiaryPets(EntityPlayer player) {
		super(player);
		this.playerExt.selectedSubspecies = 0;
	}


	@Override
	public String getTitle() {
		if(this.playerExt.selectedPet != null) {
			String title = this.playerExt.selectedPet.getDisplayName();
			if(this.playerExt.selectedPet.releaseEntity) {
				title = I18n.translateToLocal("gui.pet.release") + " " + title;
			}
			return title;
		}
		return "Pets";
	}


	@Override
	public void initControls() {
		super.initControls();

		int petTypeListHeight = Math.round((float)this.colLeftHeight * 0.225F);
		int petTypeListY = this.colLeftY;
		this.petTypeList = new GuiPetTypeList(this, this.colLeftWidth, petTypeListHeight, petTypeListY, petTypeListY + petTypeListHeight, this.colLeftX);

		int petListHeight = Math.round((float)this.colLeftHeight * 0.7F);
		int petListY = petTypeListY + petTypeListHeight + Math.round((float)this.colLeftHeight * 0.025F);
		this.petList = new GuiCreatureList(GuiCreatureList.Type.PET, this, this.petTypeList, this.colLeftWidth, petListHeight, petListY, petListY + petListHeight, this.colLeftX);

		int buttonWidth = 80;
		int buttonHeight = 20;
		int buttonSpacing = 2;
		int buttonMarginX = 2 + Math.max(Math.max(this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.actions")), this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.stance"))), this.getFontRenderer().getStringWidth(I18n.translateToLocal("gui.pet.movement")));
		int buttonX = this.colRightX + buttonMarginX;
		int buttonY = this.colRightY + this.colRightHeight - ((buttonHeight + buttonSpacing) * 3);

		// Actions:
		GuiButton button = new GuiButton(EntityCreatureBase.PET_COMMAND.ACTIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.active"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.TELEPORT.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.teleport"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.PVP.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.pvp"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.RELEASE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.release"));
		this.buttonList.add(button);

		// Stance:
		buttonX = this.colRightX + buttonMarginX;
		buttonY += buttonHeight + 2;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.PASSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.passive"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.DEFENSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.defensive"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.ASSIST.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.assist"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.AGGRESSIVE.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.aggressive"));
		this.buttonList.add(button);

		// Movement:
		buttonX = this.colRightX + buttonMarginX;
		buttonY += buttonHeight + 2;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.FOLLOW.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.follow"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.WANDER.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.wander"));
		this.buttonList.add(button);

		buttonX += buttonWidth + buttonSpacing;
		button = new GuiButton(EntityCreatureBase.PET_COMMAND.SIT.id + this.petCommandIdStart, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.pet.sit"));
		this.buttonList.add(button);

		// Release Confirmation:
		buttonX = this.colRightX + Math.round((float)this.colRightWidth / 2) - (buttonWidth + buttonSpacing);
		buttonY = this.colRightY + Math.round((float)this.colRightHeight / 2) - Math.round((float)buttonHeight / 2);
		button = new GuiButton(this.releaseConfirmId, buttonX, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("common.yes"));
		this.buttonList.add(button);

		buttonX += buttonSpacing;
		button = new GuiButton(this.releaseCancelId, buttonX + buttonWidth, buttonY, buttonWidth, buttonHeight, I18n.translateToLocal("common.no"));
		this.buttonList.add(button);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);

		this.petTypeList.drawScreen(mouseX, mouseY, partialTicks);
		this.petList.drawScreen(mouseX, mouseY, partialTicks);

		// Update Buttons:
		for(GuiButton button : this.buttonList) {
			// Pet Controls:
			if(button.id >= this.petCommandIdStart && button.id < this.releaseConfirmId) {
				if (this.playerExt.selectedPet != null && !this.playerExt.selectedPet.releaseEntity) {
					button.visible = true;

					// Actions:
					if (button.id == EntityCreatureBase.PET_COMMAND.ACTIVE.id + this.petCommandIdStart) {
						if (!this.playerExt.selectedPet.spawningActive) {
							button.displayString = I18n.translateToLocal("gui.pet.summon");
						}
						else {
							button.displayString = I18n.translateToLocal("gui.pet.dismiss");
						}
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.PVP.id + this.petCommandIdStart) {
						if (this.playerExt.selectedPet.summonSet.getPVP()) {
							button.displayString = I18n.translateToLocal("gui.pet.pvp") + ": " + I18n.translateToLocal("common.yes");
						}
						else {
							button.displayString = I18n.translateToLocal("gui.pet.pvp") + ": " + I18n.translateToLocal("common.no");
						}
					}

					// Stance:
					else if (button.id == EntityCreatureBase.PET_COMMAND.PASSIVE.id + this.petCommandIdStart) {
						button.enabled = !this.playerExt.selectedPet.summonSet.passive;
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.DEFENSIVE.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.selectedPet.summonSet.getPassive() && !this.playerExt.selectedPet.summonSet.getAssist() && !this.playerExt.selectedPet.summonSet.getAggressive());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.ASSIST.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.selectedPet.summonSet.getPassive() && this.playerExt.selectedPet.summonSet.getAssist() && !this.playerExt.selectedPet.summonSet.getAggressive());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.AGGRESSIVE.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.selectedPet.summonSet.getPassive() && this.playerExt.selectedPet.summonSet.getAggressive());
					}

					// Movement:
					else if (button.id == EntityCreatureBase.PET_COMMAND.FOLLOW.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.selectedPet.summonSet.getSitting() && this.playerExt.selectedPet.summonSet.getFollowing());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.WANDER.id + this.petCommandIdStart) {
						button.enabled = !(!this.playerExt.selectedPet.summonSet.getSitting() && !this.playerExt.selectedPet.summonSet.getFollowing());
					}
					else if (button.id == EntityCreatureBase.PET_COMMAND.SIT.id + this.petCommandIdStart) {
						button.enabled = !(this.playerExt.selectedPet.summonSet.getSitting());
					}
				}
				else {
					button.visible = false;
				}
			}

			// Release Confirmation:
			else if(button.id == this.releaseConfirmId || button.id == this.releaseCancelId) {
				button.visible = this.playerExt.selectedPet != null && this.playerExt.selectedPet.releaseEntity;
			}
		}
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int marginX = 0;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 20;
		int width = this.colRightWidth - marginX;

		// Model:
		if(this.playerExt.selectedPet != null) {
			this.renderCreature(this.playerExt.selectedPet.getCreatureInfo(), this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + Math.round((float) this.colRightHeight / 2), mouseX, mouseY, partialTicks);
		}

		// Player Spirit:
		String text = I18n.translateToLocal("gui.beastiary.player.spirit") + ": ";
		this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
		int barX = nextX + this.getFontRenderer().getStringWidth(text);
		int spiritMax = Math.round((float)this.playerExt.spiritMax / this.playerExt.spiritCharge);
		int spiritReserved = (int)Math.floor((double)this.playerExt.spiritReserved / this.playerExt.spiritCharge);
		int spiritAvailable = (int)Math.floor((double)this.playerExt.spirit / this.playerExt.spiritCharge);
		float spiritFilling = ((float)this.playerExt.spirit / this.playerExt.spiritCharge) - spiritAvailable;
		this.drawBar(AssetManager.getTexture("GUIPetSpiritEmpty"), barX, nextY, 0, 9, 9, spiritMax, 10);
		this.drawBar(AssetManager.getTexture("GUIPetSpirit"), barX, nextY, 0, 9, 9, spiritAvailable, 10);
		if(spiritFilling > 0) {
			this.drawTexture(AssetManager.getTexture("GUIPetSpiritFilling"), barX + (9 * spiritAvailable), nextY, 0, spiritFilling, 1, spiritFilling * 9, 9);
		}
		this.drawBar(AssetManager.getTexture("GUIPetSpiritUsed"), barX, nextY, 0, 9, 9, spiritReserved, -10);

		// Creature Display:
		if(this.playerExt.selectedPet != null) {
			// Spirit:
			nextY += 4 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("creature.stat.spirit") + ": ";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawLevel(this.playerExt.selectedPet.getCreatureInfo(), AssetManager.getTexture("GUIPetLevel"), nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Health:
			nextY += 4 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			if(this.playerExt.selectedPet.respawnTime <= 0) {
				text = I18n.translateToLocal("creature.stat.health") + ": ";
			}
			else {
				text = I18n.translateToLocal("creature.stat.respawning") + ": ";
			}
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			barX = nextX + this.getFontRenderer().getStringWidth(text);
			int barY = nextY - 1;
			int barWidth = (256 / 4) + 16;
			int barHeight = (32 / 4) + 2;
			this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
			if(this.playerExt.selectedPet.respawnTime <= 0) {
				float healthNormal = this.playerExt.selectedPet.getHealth() / this.playerExt.selectedPet.getMaxHealth();
				this.drawTexture(AssetManager.getTexture("GUIPetBarHealth"), barX, barY, 0, healthNormal, 1, barWidth * healthNormal, barHeight);
			}
			else {
				float respawnNormal = 1.0F - ((float)this.playerExt.selectedPet.respawnTime / this.playerExt.selectedPet.respawnTimeMax);
				this.drawTexture(AssetManager.getTexture("GUIPetBarRespawn"), barX, barY, 0, respawnNormal, 1, barWidth * respawnNormal, barHeight);
			}
		}

		// Base Display:
		else {
			nextY += 4 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = I18n.translateToLocal("gui.beastiary.selectapet");
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}

		// Button Titles:
		int buttonHeight = 20;
		int buttonSpacing = 2;
		int buttonY = this.colRightY + this.colRightHeight - ((buttonHeight + buttonSpacing) * 3);
		if(this.playerExt.selectedPet != null && !this.playerExt.selectedPet.releaseEntity) {
			this.getFontRenderer().drawString(I18n.translateToLocal("gui.pet.actions"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
			buttonY += buttonHeight + buttonSpacing;
			this.getFontRenderer().drawString(I18n.translateToLocal("gui.pet.stance"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
			buttonY += buttonHeight + buttonSpacing;
			this.getFontRenderer().drawString(I18n.translateToLocal("gui.pet.movement"), this.colRightX, buttonY + 6, 0xFFFFFF, true);
		}

		// Release Confirmation:
		if(this.playerExt.selectedPet != null && this.playerExt.selectedPet.releaseEntity) {
			text = I18n.translateToLocal("gui.pet.release.confirm");
			nextX = this.colRightX;
			nextY = this.colRightY + Math.round((float) this.colRightHeight / 2) - Math.round((float) buttonHeight / 2) - this.getFontRenderer().getWordWrappedHeight(text, this.colRightWidth) - 2;
			this.drawSplitString(text, nextX, nextY, this.colRightWidth, 0xFFFFFF, true);
		}
	}


	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(this.playerExt.selectedPet != null && this.playerExt.selectedPet.summonSet != null) {
			PetEntry petEntry = this.playerExt.selectedPet;

			// Pet Commands;
			if(button.id >= this.petCommandIdStart && button.id < this.releaseConfirmId) {
				int petCommandId = button.id - this.petCommandIdStart;

				// Actions:
				if (petCommandId == EntityCreatureBase.PET_COMMAND.ACTIVE.id) {
					petEntry.spawningActive = !petEntry.spawningActive;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.TELEPORT.id) {
					petEntry.teleportEntity = true;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.PVP.id) {
					petEntry.summonSet.pvp = !petEntry.summonSet.pvp;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.RELEASE.id) {
					petEntry.releaseEntity = true;
				}

				// Stance:
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.PASSIVE.id) {
					petEntry.summonSet.passive = true;
					petEntry.summonSet.assist = false;
					petEntry.summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.DEFENSIVE.id) {
					petEntry.summonSet.passive = false;
					petEntry.summonSet.assist = false;
					petEntry.summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.ASSIST.id) {
					petEntry.summonSet.passive = false;
					petEntry.summonSet.assist = true;
					petEntry.summonSet.aggressive = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.AGGRESSIVE.id) {
					petEntry.summonSet.passive = false;
					petEntry.summonSet.assist = true;
					petEntry.summonSet.aggressive = true;
				}

				// Movement:
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.FOLLOW.id) {
					petEntry.summonSet.following = true;
					petEntry.summonSet.sitting = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.WANDER.id) {
					petEntry.summonSet.following = false;
					petEntry.summonSet.sitting = false;
				}
				else if (petCommandId == EntityCreatureBase.PET_COMMAND.SIT.id) {
					petEntry.summonSet.following = false;
					petEntry.summonSet.sitting = true;
				}

				this.playerExt.sendPetEntryToServer(petEntry);
				if (this.playerExt.selectedPet == null) {
					openToPlayer(this.player);
				}
				return;
			}

			// Release Confirmation:
			else if(button.id == this.releaseCancelId) {
				petEntry.releaseEntity = false;
				return;
			}
			else if(button.id == this.releaseConfirmId) {
				this.playerExt.selectedPet = null;
				this.playerExt.sendPetEntryRemoveRequest(petEntry);
				return;
			}
		}

		super.actionPerformed(button);
	}


	@Override
	public int getDisplaySubspecies(CreatureInfo creatureInfo) {
		return this.playerExt.selectedPet.subspeciesID;
	}


	@Override
	public void playCreatureSelectSound(CreatureInfo creatureInfo) {
		this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, AssetManager.getSound(creatureInfo.getName() + "_tame"), SoundCategory.NEUTRAL, 1, 1);
	}
}
