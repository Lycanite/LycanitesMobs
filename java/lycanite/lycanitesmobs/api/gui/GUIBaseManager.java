package lycanite.lycanitesmobs.api.gui;

import cpw.mods.fml.client.GuiScrollingList;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUIBaseManager extends GuiScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public String type;
	public PetEntry selectedPet;

	public GuiScrollingList list;

	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBaseManager(EntityPlayer player, String type) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
		this.type = type;
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
        int listHeight = this.windowHeight - (39 + buttonSpacing) - 16; // 39 = Title Height + Spirit Height, 24 = Excess
        int listTop = this.windowY + 39 + buttonSpacing; // 39 = Title Height + Spirit Height
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);

		// Default Selection:
		if(this.hasPets()) {
			this.selectedPet = this.playerExt.petManager.getEntry(this.type, 0);
		}
        
		// Group List:
		this.list = new GUIPetList(this, this.playerExt, listWidth, listHeight, listTop, listBottom, listX);
		this.list.registerScrollButtons(this.buttonList, 51, 52);
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer(x, y, f);
        this.drawGuiContainerForegroundLayer(x, y, f);
        
        // Pet List:
		if(this.hasPets())
			this.list.drawScreen(x, y, f);

        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer(int x, int y, float f) {
		// No Pets:
		if(!this.hasPets()) {
			this.getFontRenderer().drawString(StatCollector.translateToLocal("gui." + this.type + "manager.empty"), this.centerX - 24, this.windowY + 6, 0xFFFFFF);
			this.fontRendererObj.drawSplitString(StatCollector.translateToLocal("gui." + this.type + "manager.info"), this.windowX + 16, this.windowY + 30, this.windowWidth - 32, 0xFFFFFF);
			return;
		}

		// Title:
		this.getFontRenderer().drawString(StatCollector.translateToLocal("gui." + this.type + "manager.name"), this.centerX - 24, this.windowY + 6, 0xFFFFFF);

		// Spirit Bar:
		int spiritBarWidth = 9;
		int spiritBarHeight = 9;
		int spiritBarX = this.windowX + 4;
		int spiritBarY = this.windowY + 30 - spiritBarHeight;
		int spiritBarU = 256 - spiritBarWidth;
		int spiritBarV = 256 - spiritBarHeight;

		for(int spiritBarEnergyN = 0; spiritBarEnergyN < 10; spiritBarEnergyN++) {
			this.drawTexturedModalRect(spiritBarX + (spiritBarWidth * spiritBarEnergyN), spiritBarY, spiritBarU, spiritBarV, spiritBarWidth, spiritBarHeight);
			if(this.playerExt.spirit >= this.playerExt.spiritMax - (spiritBarEnergyN * this.playerExt.spiritCharge)) {
				this.drawTexturedModalRect(spiritBarX + (spiritBarWidth * spiritBarEnergyN), spiritBarY, spiritBarU - spiritBarWidth, spiritBarV, spiritBarWidth, spiritBarHeight);
			}
			else if(this.playerExt.spirit + this.playerExt.spiritCharge > this.playerExt.spiritMax - (spiritBarEnergyN * this.playerExt.spiritCharge)) {
				float spiritChargeScale = (float)(this.playerExt.spirit % this.playerExt.spiritCharge) / (float)this.playerExt.spiritCharge;
				this.drawTexturedModalRect((spiritBarX + (spiritBarWidth * spiritBarEnergyN)) + (spiritBarWidth - Math.round((float)spiritBarWidth * spiritChargeScale)), spiritBarY, spiritBarU - Math.round((float)spiritBarWidth * spiritChargeScale), spiritBarV, Math.round((float)spiritBarWidth * spiritChargeScale), spiritBarHeight);
			}
		}
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer(int x, int y, float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.getTexture());
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
	//                   Pet Selection
	// ==================================================
	public void selectPet(PetEntry petSelection) {

		this.selectedPet = petSelection;
	}

	public PetEntry getSelectedPet() {
		return this.selectedPet;
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


	// ==================================================
	//                     Has Pets
	// ==================================================
	public boolean hasPets() {
		return this.playerExt.petManager.getEntryList(this.type) != null && this.playerExt.petManager.getEntryList(this.type).size() > 0;
	}


	// ==================================================
	//                     Get Texture
	// ==================================================
	protected ResourceLocation getTexture() {
		return AssetManager.getTexture("GUIPet");
	}
}
