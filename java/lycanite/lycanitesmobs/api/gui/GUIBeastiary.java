package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class GUIBeastiary extends GuiScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public int halfX;
	public int halfY;
	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int windowX;
	public int windowY;
	
	public GUIBeastiaryGroupList groupList;
	public GUIBeastiaryCreatureList creatureList;
	public EntityLivingBase creaturePreviewEntity;
		
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.BEASTIARY.id, 0, 0);
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
        this.halfX = this.width / 2;
        this.halfY = this.height / 2;
		this.windowWidth = 176;
        this.windowHeight = 166;
        this.windowX = this.halfX - (this.windowWidth / 2);
        this.windowY = this.halfY - (this.windowHeight / 2);
        this.centerX = this.windowX + this.halfX;
        this.centerY = this.windowY + this.halfY;
		//this.drawControls();
		
        int buttonSpacing = 2;
        
		// Group List:
		this.groupList = new GUIBeastiaryGroupList(this,
				(this.windowWidth / 2) - (buttonSpacing * 2),
				this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowY + 52,
				this.windowY + 16 + this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowX + (buttonSpacing * 2),
				20
			);
		this.groupList.registerScrollButtons(this.buttonList, 51, 52);
		
		// Creature List:
		this.creatureList = new GUIBeastiaryCreatureList(this,
				(this.windowWidth / 2) - (buttonSpacing * 2),
				this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowY + 52,
				this.windowY + 16 + this.windowHeight - 16 - (buttonSpacing * 2),
				this.windowX + (buttonSpacing * 2),
				20
			);
		this.creatureList.registerScrollButtons(this.buttonList, 53, 54);
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.drawGuiContainerForegroundLayer();
        
        // Creature List:
		this.groupList.drawScreen(x, y, f);
		this.creatureList.drawScreen(x, y, f);
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.beastiary.name"), this.windowX + 8, this.windowY + 6, 4210752);
		
		// Draw Creature Entry:
		if(this.getSelectedCreature() != null) {
			this.fontRendererObj.drawString(this.getSelectedCreature().getTitle(), this.centerX + 8, this.windowY + 6, 4210752);
			this.fontRendererObj.drawSplitString(this.getSelectedCreature().getDescription(), this.centerX + 8, this.windowY + 32 + 24, this.halfX - 16, 4210752);
			GuiInventory.func_147046_a(this.centerX + 26, this.centerY + 24, 17, (float)(this.halfX + 51) - this.halfX, (float)(this.halfY + 75 - 50) - this.halfY, this.creaturePreviewEntity);
		}
		
		// Draw Group Entry:
		else if(this.getSelectedGroup() != null) {
			this.fontRendererObj.drawString(StatCollector.translateToLocal(this.getSelectedGroup().getModID() + ".name"), this.centerX + 8, this.windowY + 6, 4210752);
			this.fontRendererObj.drawSplitString(StatCollector.translateToLocal(this.getSelectedGroup().getModID() + ".description"), this.centerX + 8, this.windowY + 24, this.halfX - 16, 4210752);
		}
		
		// Draw Soulgazer Instructions:
		else {
			this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.beastiary.empty"), this.centerX + 8, this.windowY + 6, 4210752);
			this.fontRendererObj.drawSplitString(StatCollector.translateToLocal("gui.beastiary.soulgazerinfo"), this.centerX + 8, this.windowY + 24, this.halfX - 16, 4210752);
			//TODO Draw Soulgazer crafting recipe.
		}
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
     }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
	}

	
	// ==================================================
  	//                 Creature Selection
  	// ==================================================
	public void selectGroup(ILycaniteMod group) {
		this.playerExt.beastiaryGroup = group;
		if(this.creatureList != null) {
			this.creatureList.updateList();
			this.selectCreature(null);
		}
		this.updateSelectedCreature();
	}
	
	public ILycaniteMod getSelectedGroup() {
		return this.playerExt.beastiaryGroup;
	}
	
	public void selectCreature(MobInfo mobInfo) {
		this.playerExt.beastiaryCreature = mobInfo;
		this.updateSelectedCreature();
	}
	
	public MobInfo getSelectedCreature() {
		return this.playerExt.beastiaryCreature;
	}
	
	public void updateSelectedCreature() {
		if(this.getSelectedCreature() == null)
			this.creaturePreviewEntity = null;
		else
			try {
				this.creaturePreviewEntity = (EntityLivingBase)this.getSelectedCreature().entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.player.worldObj});
			} catch (Exception e) {
				LycanitesMobs.printWarning("", "Tried to preview an invalid creature in the Beastiary.");
				e.printStackTrace();
			}
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
