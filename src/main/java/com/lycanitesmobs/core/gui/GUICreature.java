package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.inventory.ContainerBase;
import com.lycanitesmobs.core.inventory.ContainerCreature;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.network.MessageEntityGUICommand;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class GUICreature extends GUIBaseContainer {
	public EntityCreatureBase creature;
	public InventoryCreature creatureInventory;
	public InventoryPlayer playerInventory;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUICreature(EntityCreatureBase creature, InventoryPlayer playerInventory) {
		super(new ContainerCreature(creature, playerInventory));
		this.creature = creature;
		this.creatureInventory = creature.inventory;
		this.playerInventory = playerInventory;
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
		this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
		this.drawControls(backX, backY);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		this.fontRendererObj.drawString(this.creatureInventory.getName(), 8, 6, 4210752);
        this.fontRendererObj.drawString(I18n.translateToLocal(this.playerInventory.getName()), 8, this.ySize - 96 + 2, 4210752);
    }
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(backX, backY, 0, 0, this.xSize, this.ySize);

		this.drawFrames(backX, backY, i, j);
		this.drawHealth(backX, backY);
		this.drawSlots(backX, backY);
	}
	
	// ========== Draw Creature Frame ===========
	protected void drawFrames(int backX, int backY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
        // Status Frame:
        int statusWidth = 90;
        int statusHeight = 54;
        this.drawTexturedModalRect(backX + 79, backY + 17, 0, 256 - statusHeight, statusWidth, statusHeight);
        
        // Creature Frame:
        int creatureWidth = 54;
        int creatureHeight = 54;
        this.drawTexturedModalRect(backX - creatureWidth + 1, backY + 17, statusWidth, 256 - creatureHeight, creatureWidth, creatureHeight);
        GuiInventory.drawEntityOnScreen(backX + 26 - creatureWidth + 1, backY + 60, 17, (float) backX - mouseX, (float) backY - mouseY, this.creature);
	}
	
	// ========== Draw Creature Health ===========
	protected void drawHealth(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
        // Empty:
        int barWidth = 80;
        int barHeight = 11;
        int barX = backX + 91;
        int barY = backY + 5;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
        
        // Full:
        barWidth = Math.round(barWidth * (this.creature.getHealth() / this.creature.getMaxHealth()));
        barV = barV + barHeight;
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
	}
	
	// ========== Draw Slots ===========
	protected void drawSlots(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
		ContainerBase container = (ContainerBase)this.inventorySlots;
		List<Slot> creatureSlots = container.inventorySlots.subList(container.specialStart, container.inventoryFinish + 1);
		int slotWidth = 18;
		int slotHeight = 18;
		int slotU = 238;
		int slotVBase = 0;
		for(Slot creatureSlot : creatureSlots) {
			int slotX = backX + creatureSlot.xPos - 1;
			int slotY = backY + creatureSlot.yPos - 1;
			int slotV = slotVBase;
			String slotType = creatureInventory.getTypeFromSlot(creatureSlot.getSlotIndex());
			if(slotType != null) {
				if(slotType.equals("saddle"))
					slotV += slotHeight;
				else if(slotType.equals("bag"))
					slotV += slotHeight * 2;
				else if(slotType.equals("chest"))
					slotV += slotHeight * 3;
			}
			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}
	
	// ========== Draw Controls ===========
	protected void drawControls(int backX, int backY) {
		if(!(this.creature instanceof EntityCreatureTameable))
			return;
		EntityCreatureTameable pet = (EntityCreatureTameable)this.creature;
		if(!pet.petControlsEnabled())
			return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = backX + this.xSize;
        int buttonY = backY;
        
        String buttonText = I18n.translateToLocal("gui.pet.sitting") + ": " + (pet.isSitting() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));
        buttonY += buttonSpacing;
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = I18n.translateToLocal("gui.pet.movement") + ": " + (pet.isFollowing() ? I18n.translateToLocal("gui.pet.follow") : I18n.translateToLocal("gui.pet.wander"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = I18n.translateToLocal("gui.pet.passive") + ": " + (pet.isPassive() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = I18n.translateToLocal("gui.pet.stance") + ": " + (pet.isAggressive() ? I18n.translateToLocal("gui.pet.aggressive") : I18n.translateToLocal("gui.pet.defensive"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = I18n.translateToLocal("gui.pet.pvp") + ": " + (pet.isPVP() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			MessageEntityGUICommand message = new MessageEntityGUICommand((byte)guiButton.id, this.creature);
			LycanitesMobs.packetHandler.sendToServer(message);			
	    }
		super.actionPerformed(guiButton);
	}
}
