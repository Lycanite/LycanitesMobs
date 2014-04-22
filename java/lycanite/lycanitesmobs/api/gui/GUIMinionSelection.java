package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

public class GUIMinionSelection extends GuiScreen {
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
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.MINION_SELECTION.id, 0, 0);
	}
	
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinionSelection(EntityPlayer player) {
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
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
		this.windowWidth = 256;
        this.windowHeight = 256;
        this.windowX = this.centerX;
        this.windowY = this.centerY;
		this.drawControls();
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.updateControls();
        this.drawGuiContainerForegroundLayer();
        
        // Creature List:
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIMinion"));
	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 20;
        int buttonHeight = 20;
        int buttonX = this.centerX - Math.round(buttonWidth / 2);
        int buttonY = this.centerY - Math.round(buttonHeight / 2);
        GuiButton button;
        int offset = 16;
        
        button = new GuiButton(1, buttonX, buttonY - Math.round(offset * 2), buttonWidth, buttonHeight, "" + 1);
    	this.buttonList.add(button);
    	
        button = new GuiButton(2, buttonX + Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 2);
    	this.buttonList.add(button);
    	
        button = new GuiButton(3, buttonX + Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 3);
    	this.buttonList.add(button);
    	
        button = new GuiButton(4, buttonX - Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 4);
    	this.buttonList.add(button);
    	
        button = new GuiButton(5, buttonX - Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 5);
    	this.buttonList.add(button);
        
        /*for(int setID = 1; setID <= 5; setID++) {
        	float offset = ((float)setID / 6) - 0.5F;
        	offset = (float)Math.sin(offset);
        	LycanitesMobs.printDebug("", "" + offset);
        	int posX = this.centerX + Math.round(this.windowWidth * offset) - Math.round(buttonWidth / 2);
        	int posY = this.centerY + Math.round(this.windowHeight * offset) - Math.round(buttonHeight / 2);
        	GuiButton button = new GuiButton(setID, posX, posY, buttonWidth, buttonHeight, "" + setID);
        	this.buttonList.add(button);
        }*/
    }
	
	public void updateControls() {
        for(Object buttonObj : this.buttonList) {
        	if(buttonObj instanceof GuiButton) {
        		GuiButton button = (GuiButton)buttonObj;
        		button.drawButton = this.playerExt.getSummonSet(button.id).isUseable();
        		button.enabled = button.id != this.playerExt.selectedSummonSet;
        	}
        }
	}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(guiButton != null) {
			this.playerExt.setSelectedSummonSet(guiButton.id);
			Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.MINION_SELECT.id, (byte)this.playerExt.selectedSummonSet);
			PacketHandler.sendPacketToServer(packet);
		}
		super.actionPerformed(guiButton);
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode)
        	 this.mc.thePlayer.closeScreen();
		super.keyTyped(par1, par2);
	}
}
