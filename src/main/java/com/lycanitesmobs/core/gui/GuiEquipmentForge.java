package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.container.ContainerEquipmentForge;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiEquipmentForge extends GUIBaseContainer {
	public InventoryPlayer playerInventory;
	public TileEntityEquipmentForge equipmentForge;
	public String currentMode = "empty";
	public boolean confirmation = false;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GuiEquipmentForge(TileEntityEquipmentForge equipmentForge, InventoryPlayer playerInventory) {
		super(new ContainerEquipmentForge(equipmentForge, playerInventory));
		this.playerInventory = playerInventory;
		this.equipmentForge = equipmentForge;
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
		this.fontRenderer.drawString(this.equipmentForge.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal(this.playerInventory.getName()), 8, this.ySize - 96 + 2, 4210752);
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
		this.drawSlots(backX, backY);
	}
	
	// ========== Draw Frames ===========
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
	}
	
	// ========== Draw Slots ===========
	protected void drawSlots(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
		/*ContainerBase container = (ContainerBase)this.inventorySlots;
		List<Slot> forgeSlots = container.inventorySlots.subList(container.inventoryStart, container.inventoryFinish);
		int slotWidth = 18;
		int slotHeight = 18;
		int slotU = 238;
		int slotVBase = 0;
		for(Slot forgeSlot : forgeSlots) {
			int slotX = backX + forgeSlot.xPos - 1;
			int slotY = backY + forgeSlot.yPos - 1;
			int slotV = slotVBase;
			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}*/
	}
	
	// ========== Draw Controls ===========
	protected void drawControls(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = backX + this.xSize;
        int buttonY = backY;
        
        String buttonText = "";
        if("construct".equals(this.currentMode)) {
			buttonText = I18n.translateToLocal("gui.equipmentforge.forge");
		}
		else if("deconstruct".equals(this.currentMode)) {
			buttonText = I18n.translateToLocal("gui.equipmentforge.deconstruct");
		}
        buttonY += buttonSpacing;
        this.buttonList.add(new GuiButton(1, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			MessageTileEntityButton message = new MessageTileEntityButton((byte)guiButton.id, this.equipmentForge.getPos());
			LycanitesMobs.packetHandler.sendToServer(message);
	    }
		super.actionPerformed(guiButton);
	}
}
