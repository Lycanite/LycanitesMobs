package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.SummonSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.GuiScrollingList;

public class GUIMinion extends GuiScreen {
	public static int tabButtonID = 100;
	
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public int editSet;
	public SummonSet summonSet;
	
	GuiScrollingList list;
	
	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public static void openToPlayer(EntityPlayer player, int editSet) {
		if(player != null && player.worldObj != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.MINION_CONTROLS.id, editSet, 0);
	}
	
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinion(EntityPlayer player, int editSet) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.extendedPlayers.get(player);
		this.editSet = editSet;
		this.summonSet = this.playerExt.getSummonSet(editSet);
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
        super.drawScreen(x, y, f);
        this.drawGuiContainerForegroundLayer();
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString("Minion Manager", this.windowX + 52, this.windowY + 6, 4210752);
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
        int buttonWidth = 28;
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY + 16;
        
        // Tabs:
        int tabCount = 5;
        int tabSpacing = buttonSpacing;
        for(int i = 1; i <= tabCount; i++) {
	        String buttonText = String.valueOf(i);
	        buttonX += tabSpacing;
	        GuiButton tabButton = new GuiButton(tabButtonID + i, buttonX, buttonY, buttonWidth, buttonHeight, buttonText);
	        this.buttonList.add(tabButton);
	        if(i == this.editSet)
	        	tabButton.enabled = false;
	        tabSpacing = buttonWidth + buttonSpacing;
        }
        
        // Creature List:
		this.list = new GuiScrollingList(this.mc,
				(this.windowWidth / 2) - (buttonSpacing * 2),
				this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowY + 16,
				this.windowY + 16 + this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowX + buttonSpacing,
				20
			);
		//TODO Make my own subclass of this then use that for the scrolling goodness!
        
        // Behaviour:
        buttonWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        buttonX = this.centerX + 6;
        buttonY = this.windowY + 16;
        
        String buttonText = "Sitting: " + (summonSet.getSitting() ? "Yes" : "No");
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "Movement: " + (summonSet.getFollowing() ? "Follow" : "Wander");
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id, buttonX, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "Passive: " + (summonSet.getPassive() ? "Yes" : "No");
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "Stance: " + (summonSet.getAggressive() ? "Aggressive" : "Defensive");
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonX, buttonY, buttonWidth, buttonHeight, buttonText));
        
        buttonText = "PvP: " + (summonSet.getPVP() ? "Yes" : "No");
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(guiButton != null) {
			// Behaviour Button:
			if(guiButton.id < this.tabButtonID) {
				if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.SITTING.id)
					this.summonSet.sitting = !this.summonSet.sitting;
				if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id)
					this.summonSet.following = !this.summonSet.following;
				if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id)
					this.summonSet.passive = !this.summonSet.passive;
				if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.STANCE.id)
					this.summonSet.aggressive = !this.summonSet.aggressive;
				if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.PVP.id)
					this.summonSet.pvp = !this.summonSet.pvp;
		        this.playerExt.sendSummonSetToServer((byte)this.editSet);
			}
			
			// Tab Button:
			if(guiButton.id >= this.tabButtonID) {
				this.editSet = guiButton.id - this.tabButtonID;
				this.summonSet = this.playerExt.getSummonSet(this.editSet);
			}
		}
		super.actionPerformed(guiButton);
		openToPlayer(this.player, this.editSet);
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
