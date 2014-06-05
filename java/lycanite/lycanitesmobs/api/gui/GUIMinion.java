package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.SummonSet;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.GuiScrollingList;

public class GUIMinion extends GuiScreen {
	public static int tabButtonID = 100;
	
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public int editSet;
	public SummonSet summonSet;
	
	public GuiScrollingList list;
	
	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;
	
	// ==================================================
  	//                      Opener
  	// ==================================================
	public static void openToPlayer(EntityPlayer player, int editSet) {
		if(player != null && player.worldObj != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.MINION_CONTROLS.id, editSet, 0);
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}
	
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinion(EntityPlayer player, int editSet) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
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
		
		// Creature List:
        int buttonSpacing = 2;
		this.list = new GUIMinionList(this, this.playerExt,
				(this.windowWidth / 2) - (buttonSpacing * 2),
				this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowY + 52,
				this.windowY + 16 + this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowX + (buttonSpacing * 2),
				20
			);
		this.list.registerScrollButtons(this.buttonList, 51, 52);
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
		this.list.drawScreen(x, y, f);
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.getFontRenderer().drawString(StatCollector.translateToLocal("gui.minion.name"), this.windowX + 52, this.windowY + 6, 4210752);
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
        int buttonWidth = 32;
        int buttonHeight = 32;
        int buttonX = this.windowX + 2;
        int buttonY = this.windowY + 16;
        
        // Tabs:
        int tabCount = 5;
        int tabSpacing = buttonSpacing;
        for(int i = 1; i <= tabCount; i++) {
	        String buttonText = String.valueOf(i);
	        MobInfo mobInfo = this.playerExt.getSummonSet(i).getMobInfo();
	        buttonX += tabSpacing;
	        GuiButton tabButton = new GUIButtonCreature(tabButtonID + i, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, mobInfo);
	        this.buttonList.add(tabButton);
	        if(i == this.editSet)
	        	tabButton.enabled = false;
	        tabSpacing = buttonWidth + buttonSpacing;
        }
        
        // Behaviour:
        buttonSpacing = 1;
        buttonWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        buttonHeight = 20;
        buttonX = this.centerX + buttonSpacing;
        buttonY = this.windowY + 30;
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX, buttonY, buttonWidth, buttonHeight, "Loading"));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id, buttonX, buttonY, buttonWidth, buttonHeight, "Loading"));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "Loading"));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonX, buttonY, buttonWidth, buttonHeight, "Loading"));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX, buttonY, buttonWidth, buttonHeight, "Loading"));
    }
	
	public void updateControls() {
        for(Object buttonObj : this.buttonList) {
        	if(buttonObj instanceof GuiButton) {
        		GuiButton button = (GuiButton)buttonObj;
        		
        		// Behaviour Buttons:
        		if(button.id < this.tabButtonID) {
	        		if(button.id == EntityCreatureBase.GUI_COMMAND_ID.SITTING.id)
	        			button.displayString = StatCollector.translateToLocal("gui.pet.sitting") + ": " + (this.summonSet.getSitting() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
	        		
	        		if(button.id == EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id)
	        			button.displayString = (this.summonSet.getFollowing() ? StatCollector.translateToLocal("gui.pet.follow") : StatCollector.translateToLocal("gui.pet.wander"));
	        		
	        		if(button.id == EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id)
	        			button.displayString = StatCollector.translateToLocal("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
	        		
	        		if(button.id == EntityCreatureBase.GUI_COMMAND_ID.STANCE.id)
	        			button.displayString = (this.summonSet.getAggressive() ? StatCollector.translateToLocal("gui.pet.aggressive") : StatCollector.translateToLocal("gui.pet.defensive"));
	        		
	        		if(button.id == EntityCreatureBase.GUI_COMMAND_ID.PVP.id)
	        			button.displayString = StatCollector.translateToLocal("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));
        		}
        		
        		// Tabs:
        		if(button.id >= this.tabButtonID) {
        			button.enabled = button.id - this.tabButtonID != this.editSet;
        		}
        	}
        }
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
		//openToPlayer(this.player, this.editSet);
	}
	
	public void selectMinion(String minionName) {
		this.summonSet.setSummonType(minionName);
		this.playerExt.sendSummonSetToServer((byte)this.editSet);
		for(Object buttonObj : this.buttonList) {
			GuiButton button = (GuiButton)buttonObj;
			if(button instanceof GUIButtonCreature && button.id == this.editSet + this.tabButtonID) {
				MobInfo mobInfo = this.playerExt.getSummonSet(this.editSet).getMobInfo();
				((GUIButtonCreature)button).mobInfo = mobInfo;
			}
		}
	}
	
	public String getSelectedMinion() {
		return this.summonSet.summonType;
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
