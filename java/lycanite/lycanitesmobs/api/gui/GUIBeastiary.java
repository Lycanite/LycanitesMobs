package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.packet.PacketBeastiary;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

public class GUIBeastiary extends GuiScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiary(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
        this.buttonList.clear();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
		this.windowWidth = 176;
        this.windowHeight = 166;
        this.windowX = this.centerX - (this.windowWidth / 2);
        this.windowY = this.centerY - (this.windowHeight / 2);
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
		this.fontRendererObj.drawString("Minion Manager", this.windowX + 52, this.windowY + 6, 4210752);
    }
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIMinion"));
        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);
        
	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY;
        
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
			PacketBeastiary packet = new PacketBeastiary();
			packet.readBeastiary(this.playerExt.getBeastiary());
			// Don't send yet as this GUI is not yet implemented!
		}
		super.actionPerformed(guiButton);
	}
}
