package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.network.MessageGUIRequest;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import lycanite.lycanitesmobs.api.pets.SummonSet;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GUIFamiliar extends GUIBaseScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
    public PetEntry petEntry;
	public SummonSet summonSet;

	public int familiarListSize = 0;
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
  	//                      Opener
  	// ==================================================
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null) {
            player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id, 0, 0);
            MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.FAMILIAR_MANAGER.id);
            LycanitesMobs.packetHandler.sendToServer(message);
        }
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
	public GUIFamiliar(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
        this.familiarListSize = this.playerExt.petManager.familiars.size();
        if(this.hasFamiliars()) {
            this.petEntry = this.playerExt.petManager.getEntry("familiar", 0);
            this.summonSet = this.petEntry.summonSet;
        }
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
        this.halfX = this.windowWidth / 2;
        this.halfY = this.windowHeight / 2;
        this.windowX = this.centerX - (this.windowWidth / 2);
        this.windowY = this.centerY - (this.windowHeight / 2);
		this.drawControls();
		
		// Creature List:
		if(this.hasFamiliars()) {
	        int buttonSpacing = 2;
			this.list = new GUIFamiliarList(this, this.playerExt,
					(this.windowWidth / 2) - (buttonSpacing * 2),
					this.windowHeight - 16 - (buttonSpacing * 2),
					this.windowY + 16,
					this.windowY + this.windowHeight - 28 - (buttonSpacing * 2),
					this.windowX + (buttonSpacing * 2)
				);
			this.list.registerScrollButtons(this.buttonList, 51, 52);
		}
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.updateControls();
        this.drawGuiContainerForegroundLayer();

        if(this.list != null)
        	this.list.drawScreen(x, y, f);
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(this.hasFamiliars()) {
			this.getFontRenderer().drawString(I18n.translateToLocal("gui.familiarmanager.name"), this.windowX + 52, this.windowY + 6, 0xFFFFFF);
			return;
		}

        this.getFontRenderer().drawString(I18n.translateToLocal("gui.familiarmanager.empty"), this.windowX + 18, this.windowY + 6, 0xFFFFFF);
        this.fontRendererObj.drawSplitString(I18n.translateToLocal("gui.familiarmanager.info"), this.windowX + 16, this.windowY + 24, this.windowWidth - 32, 0xFFFFFF);
    }
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIFamiliar"));
        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);

        if(this.petEntry == null)
            return;

        // Health and Respawn Bar:
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));

        int barWidth = 80;
        int barHeight = 11;
        int barX = this.centerX + 2;
        int barY = this.windowY + 16;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);

        if(!this.petEntry.isRespawning) {
            barWidth = Math.round(barWidth * (this.petEntry.getHealth() / this.petEntry.getMaxHealth()));
            barV = barV + barHeight;
        }
        else {
            barWidth = barWidth - Math.round(barWidth * ((float)this.petEntry.respawnTime / (float)this.petEntry.respawnTimeMax));
            barV = barV - barHeight;
        }
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 1;
        int buttonWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int buttonHeight = 20;
        int buttonY = this.windowY + 5;
        int buttonX = this.windowX + (buttonSpacing * 2);

        this.buttonList.add(new GUITabMain(55555, buttonX, this.windowY - 27));

        if(!this.hasFamiliars()) {
            this.buttonList.add(new GuiButton(100, this.windowX + (this.windowWidth / 2) - (buttonWidth / 2), this.windowY + this.windowHeight - buttonHeight - 16, buttonWidth, buttonHeight, "Patreon"));
            return;
        }

        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SPAWNING.id, buttonX, this.windowY + this.windowHeight - buttonHeight - 9, buttonWidth, buttonHeight, "..."));

        buttonX = this.centerX + buttonSpacing;

        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.TELEPORT.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
    }
	
	public void updateControls() {
		if(!this.hasFamiliars()) return;
		
        for(Object buttonObj : this.buttonList) {
        	if(buttonObj instanceof GuiButton) {
        		GuiButton button = (GuiButton)buttonObj;

                // Action Buttons:
                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.SPAWNING.id)
                    button.displayString = I18n.translateToLocal("gui.pet.active") + ": " + (this.petEntry.spawningActive ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));

                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.TELEPORT.id)
                    button.displayString = I18n.translateToLocal("gui.pet.teleport");

                // Behaviour Buttons:
                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.SITTING.id)
                    button.displayString = I18n.translateToLocal("gui.pet.sitting") + ": " + (this.summonSet.getSitting() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));

                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.FOLLOWING.id)
                    button.displayString = (this.summonSet.getFollowing() ? I18n.translateToLocal("gui.pet.follow") : I18n.translateToLocal("gui.pet.wander"));

                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id)
                    button.displayString = I18n.translateToLocal("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));

                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.STANCE.id)
                    button.displayString = (this.summonSet.getAggressive() ? I18n.translateToLocal("gui.pet.aggressive") : I18n.translateToLocal("gui.pet.defensive"));

                if(button.id == EntityCreatureBase.GUI_COMMAND_ID.PVP.id)
                    button.displayString = I18n.translateToLocal("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? I18n.translateToLocal("common.yes") : I18n.translateToLocal("common.no"));
        	}
        }
	}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			// Behaviour Button:
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

            // Action Button:
            if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.TELEPORT.id)
                this.petEntry.teleportEntity = true;
            if(guiButton.id == EntityCreatureBase.GUI_COMMAND_ID.SPAWNING.id)
                this.petEntry.spawningActive = !this.petEntry.spawningActive;

            // Patreon Button:
            if(guiButton.id == 100) {
                try {
                    this.openURI(new URI(LycanitesMobs.websitePatreon));
                } catch (URISyntaxException e) {}
            }

            if(guiButton.id < 100)
                this.playerExt.sendPetEntryToServer(this.petEntry);
		}
		super.actionPerformed(guiButton);
	}


    // ==================================================
    //                 Familiar Selection
    // ==================================================
	public void selectPetEntry(PetEntry petEntry) {
        this.petEntry = this.playerExt.petManager.getEntry(petEntry.petEntryID);
		this.summonSet = this.petEntry.summonSet;
	}
	
	public PetEntry getSelectedPetEntry() {
		return this.petEntry;
	}
	
	
	// ==================================================
  	//                     Familiars
  	// ==================================================
	public boolean hasFamiliars() {
		return this.familiarListSize > 0;
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        	 this.mc.thePlayer.closeScreen();
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
