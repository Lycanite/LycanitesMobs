package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUIMountManager extends GuiScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

	public EntityLivingBase creaturePreviewEntity;

	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.MOUNT_MANAGER.id, 0, 0);
	}

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMountManager(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
		
        this.buttonList.clear();
		this.windowWidth = 256;
        this.windowHeight = 172;
        this.halfX = this.windowWidth / 2;
        this.halfY = this.windowHeight / 2;
        this.windowX = (this.width / 2) - (this.windowWidth / 2);
        this.windowY = (this.height / 2) - (this.windowHeight / 2);
        this.centerX = this.windowX + (this.windowWidth / 2);
        this.centerY = this.windowY + (this.windowHeight / 2);
		this.drawControls();
		
        int buttonSpacing = 2;
        int listWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int listHeight = (this.windowHeight / 2) - 6 - (buttonSpacing * 3);
        int listTop = this.windowY - (buttonSpacing * 2) + 24;
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);
        
		// Group List:
		//this.creatureList = new GUIBeastiaryGroupList(this, listWidth, listHeight, listTop, listBottom, listX, 16);
		//this.creatureList.registerScrollButtons(this.buttonList, 51, 52);
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer(x, y, f);
        this.drawGuiContainerForegroundLayer(x, y, f);
        
        // Creature List:
		//this.creatureList.drawScreen(x, y, f);
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer(int x, int y, float f) {
		this.getFontRenderer().drawString(StatCollector.translateToLocal("gui.mountmanager.name"), this.centerX - 24, this.windowY + 6, 0xFFFFFF);
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer(int x, int y, float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIPet"));
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

		this.buttonList.add(new GUITabMain(55555, buttonX, buttonY - 24));
     }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        	 this.mc.thePlayer.closeScreen();
		super.keyTyped(par1, par2);
	}
	
	
	// ==================================================
  	//                     Draw Image
  	// ==================================================
	public void drawImage(int x, int y, int u, int v, int w, int h, float s, float t) {
		float z = this.zLevel;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + h), (double)z, (double)((float)(u + 0) * s), (double)((float)(v + h) * t));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h), (double)z, (double)((float)(u + w) * s), (double)((float)(v + h) * t));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + 0), (double)z, (double)((float)(u + w) * s), (double)((float)(v + 0) * t));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)z, (double)((float)(u + 0) * s), (double)((float)(v + 0) * t));
        tessellator.draw();
    }
}
