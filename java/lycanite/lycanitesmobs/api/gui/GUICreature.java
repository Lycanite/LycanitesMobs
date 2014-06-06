package lycanite.lycanitesmobs.api.gui;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.inventory.ContainerBase;
import lycanite.lycanitesmobs.api.inventory.ContainerCreature;
import lycanite.lycanitesmobs.api.inventory.InventoryCreature;
import lycanite.lycanitesmobs.api.packet.PacketEntityGUICommand;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GUICreature extends GuiContainer {
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
		this.fontRendererObj.drawString(this.creatureInventory.getInventoryName(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getInventoryName(), 8, this.ySize - 96 + 2, 4210752);
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
        GuiInventory.func_147046_a(backX + 26 - creatureWidth + 1, backY + 60, 17, (float)backX - mouseX, (float)backY - mouseY, this.creature);
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
			int slotX = backX + creatureSlot.xDisplayPosition - 1;
			int slotY = backY + creatureSlot.yDisplayPosition - 1;
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
        
        String buttonText = StatCollector.translateToLocal("gui.pet.sitting") + ": " + (pet.isSitting() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
        buttonY += buttonSpacing;
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = StatCollector.translateToLocal("gui.pet.movement") + ": " + (pet.isFollowing() ? StatCollector.translateToLocal("gui.pet.follow") : StatCollector.translateToLocal("gui.pet.wander"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = StatCollector.translateToLocal("gui.pet.passive") + ": " + (pet.isPassive() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = StatCollector.translateToLocal("gui.pet.stance") + ": " + (pet.isAggressive() ? StatCollector.translateToLocal("gui.pet.aggressive") : StatCollector.translateToLocal("gui.pet.defensive"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = StatCollector.translateToLocal("gui.pet.pvp") + ": " + (pet.isPVP() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(guiButton != null) {
			PacketEntityGUICommand packet = new PacketEntityGUICommand();
			packet.readGUICommand((byte)guiButton.id, this.creature);
			LycanitesMobs.packetPipeline.sendToServer(packet);			
	    }
		super.actionPerformed(guiButton);
	}
}
