package lycanite.lycanitesmobs.api.gui;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.inventory.ContainerBase;
import lycanite.lycanitesmobs.api.inventory.ContainerCreature;
import lycanite.lycanitesmobs.api.inventory.InventoryCreature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

public class GUICreature extends GuiContainer {
	public EntityCreatureBase creature;
	public InventoryCreature creatureInventory;
	public InventoryPlayer playerInventory;
	public static enum ButtonType {
		NULL((byte)-1), SITTING((byte)0), FOLLOWING((byte)1), STANCE((byte)2), PVP((byte)3);
		public byte id;
		private ButtonType(byte i) { id = i; }
	}
	
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
		this.fontRenderer.drawString(this.creatureInventory.getInvName(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.isInvNameLocalized() ? this.playerInventory.getInvName() : I18n.getString(this.playerInventory.getInvName()), 8, this.ySize - 96 + 2, 4210752);
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

		this.drawFrames(backX, backY);
		this.drawHealth(backX, backY);
		this.drawSlots(backX, backY);
		this.drawControls(backX, backY);
	}
	
	// ========== Draw Creature Frame ===========
	protected void drawFrames(int backX, int backY) {
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
        GuiInventory.func_110423_a(backX + 26 - creatureWidth + 1, backY + 60, 17, (float)(backX + 51) - this.xSize, (float)(backY + 75 - 50) - this.ySize, this.creature);
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 320;
        int buttonHeight = 20;
        
        String buttonText = "Sitting: " + (pet.isSitting() ? "Yes" : "No");
        backY += buttonSpacing;
        this.buttonList.add(new GuiButton(ButtonType.SITTING.id, backX + buttonSpacing, backY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "Movement: " + (pet.isFollowing() ? "Follow" : "Wander");
        backY += buttonSpacing;
        this.buttonList.add(new GuiButton(ButtonType.SITTING.id, backX + buttonSpacing, backY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "Stance: " + (pet.isPassive() ? "Passive" : "Defensive");
        backY += buttonSpacing;
        this.buttonList.add(new GuiButton(ButtonType.STANCE.id, backX + buttonSpacing, backY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "PvP: " + (pet.isPVP() ? "On" : "Off");
        backY += buttonSpacing;
        this.buttonList.add(new GuiButton(ButtonType.PVP.id, backX + buttonSpacing, backY, buttonWidth, buttonHeight, buttonText));
    }
}
