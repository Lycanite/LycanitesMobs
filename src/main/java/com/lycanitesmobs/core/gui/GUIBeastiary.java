package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GUIBeastiary extends GUIBaseScreen {
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
	
	public GUIBeastiaryGroupList groupList;
	public GUIBeastiaryCreatureList creatureList;
	public EntityLivingBase creaturePreviewEntity;
		
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.getEntityWorld() != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.getEntityWorld(), GuiHandler.PlayerGuiType.BEASTIARY.id, 0, 0);
	}
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiary(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
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
		this.updateSelectedCreature();
		
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
		this.groupList = new GUIBeastiaryGroupList(this, listWidth, listHeight, listTop, listBottom, listX);
		this.groupList.registerScrollButtons(this.buttonList, 51, 52);
		
		listTop += listHeight + buttonSpacing;
		listBottom += listHeight + buttonSpacing;
		
		// Creature List:
		this.creatureList = new GUIBeastiaryCreatureList(this, listWidth, listHeight, listTop, listBottom, listX);
		this.creatureList.registerScrollButtons(this.buttonList, 53, 54);
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer(x, y, f);
		this.updateControls();
        this.drawGuiContainerForegroundLayer(x, y, f);
        
        // Creature List:
		this.groupList.drawScreen(x, y, f);
		this.creatureList.drawScreen(x, y, f);
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer(int x, int y, float f) {
		boolean hasSomeKnowledge = this.playerExt.beastiary.creatureKnowledgeList.size() > 0;
		this.fontRenderer.drawString(I18n.translateToLocal("gui.beastiary.name"), this.windowX + 24, this.windowY + 8, 0xFFFFFF);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		// Draw Creature Entry:
		if(this.getSelectedCreature() != null && this.creaturePreviewEntity != null && hasSomeKnowledge) {
			int creatureSize = 17;
			int creatureScale = Math.round((1.8F / this.creaturePreviewEntity.height) * creatureSize);
			int creatureX = this.centerX + (this.halfX / 2);
			int creatureY = this.windowY + 32 + creatureSize;
			// X, Y, Scale, RotX, RotY, RotHead, EntityLivingBase
			GuiInventory.drawEntityOnScreen(creatureX, creatureY, creatureScale, (float) (creatureX) - x, (float) (creatureY) - y, this.creaturePreviewEntity);
			
			// Title:
			this.fontRenderer.drawString(this.getSelectedCreature().getTitle(), this.centerX + 8, this.windowY + 8, 0xFFFFFF);
			
			// Description:
			this.fontRenderer.drawSplitString(this.getSelectedCreature().getDescription(), this.centerX + 8, creatureY + creatureSize + 2, this.halfX - 16, 0xFFFFFF);
		}
		
		// Draw Group Entry:
		else if(this.getSelectedGroup() != null && hasSomeKnowledge) {
			this.fontRenderer.drawString(I18n.translateToLocal(this.getSelectedGroup().filename + ".name"), this.centerX + 8, this.windowY + 8, 0xFFFFFF);
			this.fontRenderer.drawSplitString(I18n.translateToLocal(this.getSelectedGroup().filename + ".description"), this.centerX + 8, this.windowY + 24, this.halfX - 16, 0xFFFFFF);
		}
		
		// Draw Soulgazer Instructions:
		else if(hasSomeKnowledge) {
			this.fontRenderer.drawString("", this.centerX + 8, this.windowY + 8, 0xFFFFFF);
			this.fontRenderer.drawSplitString(I18n.translateToLocal("gui.beastiary.selectacreature"), this.centerX + 8, this.windowY + 24, this.halfX - 16, 0xFFFFFF);
		}
		
		// Draw Soulgazer Instructions:
		else {
			this.fontRenderer.drawString(I18n.translateToLocal("gui.beastiary.empty"), this.centerX + 8, this.windowY + 8, 0xFFFFFF);
			this.fontRenderer.drawSplitString(I18n.translateToLocal("gui.beastiary.soulgazerinfo"), this.centerX + 8, this.windowY + 24, this.halfX - 16, 0xFFFFFF);
			int recipeWidth = 108;
			int recipeHeight = 54;
			this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
	        this.drawTexturedModalRect(this.centerX + (this.halfX / 2) - (recipeWidth / 2), this.windowY + this.windowHeight - recipeHeight - 4, 0, 256 - recipeHeight, recipeWidth, recipeHeight);
		}
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer(int x, int y, float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);

		// Level:
		if(this.getSelectedCreature() != null) {
			int levelBarWidth = 9;
			int levelBarHeight = 9;
			int levelBarX = this.centerX + 8;
			int levelBarY = this.windowY + 64 - levelBarHeight;
			int levelBarU = 256 - (levelBarWidth * 2);
			int levelBarV = 256 - levelBarHeight;
			int level = this.getSelectedCreature().summonCost;

			if(level <= 10) {
				for (int currentLevel = 0; currentLevel < level; currentLevel++) {
					this.drawTexturedModalRect(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight);
				}
			}
		}
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
		this.buttonList.add(new GuiButton(100, this.centerX - (buttonWidth / 2), this.windowY + this.windowHeight + 4, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.website")));

     }

	public void updateControls() {}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		// Website Button:
		if(guiButton.id == 100) {
			try {
				String url = LycanitesMobs.website + "/creature";
				if(this.getSelectedCreature() != null)
					url += "/" + this.getSelectedCreature().getName();
				this.openURI(new URI(url));
			} catch (URISyntaxException e) {}
		}

		super.actionPerformed(guiButton);
	}

	
	// ==================================================
  	//                 Creature Selection
  	// ==================================================
	public void selectCategory(String category) {
		this.playerExt.beastiaryCategory = category; 
	}
	
	public void selectGroup(GroupInfo group) {
		this.playerExt.beastiaryGroup = group;
		this.playerExt.beastiaryCategory = "group"; 
		if(this.creatureList != null) {
			this.creatureList.updateList();
			this.selectCreature(null);
		}
		this.updateSelectedCreature();
	}
	
	public GroupInfo getSelectedGroup() {
		return this.playerExt.beastiaryGroup;
	}
	
	public void selectCreature(CreatureInfo mobInfo) {
		this.playerExt.beastiaryCreature = mobInfo;
		this.updateSelectedCreature();
	}
	
	public CreatureInfo getSelectedCreature() {
		return this.playerExt.beastiaryCreature;
	}
	
	public void updateSelectedCreature() {
		if(this.getSelectedCreature() == null)
			this.creaturePreviewEntity = null;
		else
			try {
				this.creaturePreviewEntity = this.getSelectedCreature().entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.player.getEntityWorld()});
				this.creaturePreviewEntity.onGround = true;
				if(this.creaturePreviewEntity instanceof EntityCreatureAgeable) {
					((EntityCreatureAgeable)this.creaturePreviewEntity).setGrowingAge(0);
				}
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "Tried to preview an invalid creature in the Beastiary.");
				e.printStackTrace();
			}
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        	 this.mc.player.closeScreen();
		super.keyTyped(par1, par2);
	}


	// ==================================================
	//                     Open URI
	// ==================================================
	private void openURI(URI uri) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[]{uri});
		}
		catch (Throwable throwable) {
			LycanitesMobs.printWarning("", "Unable to open link: " + uri.toString());
		}
	}
}
