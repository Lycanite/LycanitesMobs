package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.network.MessageSummonSetSelection;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUILMMainMenu extends GuiScreen {
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
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.LM_MAIN_MENU.id, 0, 0);
	}

	public boolean doesGuiPauseGame() {
        return false;
    }

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}


	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUILMMainMenu(EntityPlayer player) {
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
        this.updateControls();
        this.drawGuiContainerForegroundLayer();
        
        // Creature List:
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.getFontRenderer().drawString(LycanitesMobs.name, this.windowX + 52, this.windowY + 6, 0xFFFFFF);
	}
	
	
	// ==================================================
	//                    Background
	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUILMMainMenu"));
		this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);
	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = this.windowWidth / 2;
        int buttonHeight = 20;
        int buttonX = this.centerX - Math.round(buttonWidth / 2);
        int buttonY = this.centerY - Math.round(buttonHeight / 2);
        GuiButton button;
        MobInfo mobInfo;
        int nextY = this.windowY + buttonHeight + buttonSpacing;

		// Beastiary:
        button = new GuiButton(GuiHandler.PlayerGuiType.BEASTIARY.id, buttonX, nextY, buttonWidth, buttonHeight, "Beastiary");
    	this.buttonList.add(button);

		// Pet Manager:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.PET_MANAGER.id, buttonX, nextY, buttonWidth, buttonHeight, "Pets");
		this.buttonList.add(button);

		// Mount Manager:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.MOUNT_MANAGER.id, buttonX, nextY, buttonWidth, buttonHeight, "Mounts");
		this.buttonList.add(button);

		// Minion Manager:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.MINION_MANAGER.id, buttonX, nextY, buttonWidth, buttonHeight, "Minions");
		this.buttonList.add(button);

		// Familiar Manager:
		nextY += buttonHeight + buttonSpacing;
		button = new GuiButton(GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id, buttonX, nextY, buttonWidth, buttonHeight, "Familiars");
		this.buttonList.add(button);
    }

	public void updateControls() {}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(guiButton != null) {
			if(guiButton.id == GuiHandler.PlayerGuiType.BEASTIARY.id) {
				GUIBeastiary.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.PET_MANAGER.id) {
				//GUIPetManager.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.MOUNT_MANAGER.id) {
				//GUIMountManager.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.MINION_MANAGER.id) {
				GUIMinion.openToPlayer(this.player, this.playerExt.selectedSummonSet);
			}
			if(guiButton.id == GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id) {
				GUIFamiliar.openToPlayer(this.player);
			}
		}
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
}
