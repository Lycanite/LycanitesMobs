package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

public class GUIMinion extends GuiScreen {
	public EntityPlayer player;
	int xSize;
	int ySize;
	int backX;
	int backY;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinion(EntityPlayer player) {
		super();
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
        this.buttonList.clear();
		this.xSize = 176;
        this.ySize = 166;
        this.backX = (this.width - this.xSize) / 2;
        this.backY = (this.height - this.ySize) / 2;
		this.drawControls();
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.drawGuiContainerForegroundLayer();
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString("Minion Manager", 8, 6, 4210752);
    }
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIMinion"));
        this.drawTexturedModalRect(this.backX, this.backY, 0, 0, this.xSize, this.ySize);
        
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = this.backX + this.xSize;
        int buttonY = this.backY;
        
        String buttonText = "A button. Press it.";
        buttonY += buttonSpacing;
        this.buttonList.add(new GuiButton(0, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(guiButton != null) {
	        Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.GUI_BUTTON, Byte.valueOf((byte)guiButton.id));
	        PacketHandler.sendPacketToServer(packet);
		}
		super.actionPerformed(guiButton);
	}
}
