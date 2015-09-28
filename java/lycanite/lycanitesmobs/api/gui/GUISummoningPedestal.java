package lycanite.lycanitesmobs.api.gui;

import cpw.mods.fml.client.GuiScrollingList;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.network.MessageGUIRequest;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import lycanite.lycanitesmobs.api.pets.SummonSet;
import lycanite.lycanitesmobs.api.tileentity.TileEntitySummoningPedestal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GUISummoningPedestal extends GUIBaseManager {
    public TileEntitySummoningPedestal summoningPedestal;


    // ==================================================
    //                      Opener
    // ==================================================
    public static void openToPlayer(EntityPlayer player, int x, int y, int z) {
        if(player != null && player.worldObj != null) {
            player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.TILEENTITY.id, player.worldObj, x, y, z);
        }
    }

    // ==================================================
    //                    Constructor
    // ==================================================
    public GUISummoningPedestal(EntityPlayer player, TileEntitySummoningPedestal summoningPedestal) {
        super(player, "minion");
        this.summoningPedestal = summoningPedestal;
        this.summonSet = this.summoningPedestal.summonSet;
    }


    // ==================================================
    //                       Init
    // ==================================================
    @Override
    public void initList() {
        // Default Selection: TODO: get active minion from Pedestal.
        if(this.hasPets() && this.summoningPedestal.summonSet != null) {
            this.selectMinion(this.summoningPedestal.summonSet.summonType);
        }

        int buttonSpacing = 2;
        int listWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int listHeight = this.windowHeight - (39 + buttonSpacing) - 16; // 39 = Title Height + Spirit Height, 24 = Excess
        int listTop = this.windowY + 39 + buttonSpacing; // 39 = Title Height + Spirit Height
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);

        this.list = new GUISummoningPedestalList(this, this.playerExt, listWidth, listHeight, listTop, listBottom, listX);
        this.list.registerScrollButtons(this.buttonList, 51, 52);
    }


    // ==================================================
    //                    Foreground
    // ==================================================
    @Override
    public String getTitle() {
        return StatCollector.translateToLocal("gui." + "summoningpedestal.name");
    }

    @Override
    public String getEnergyTitle() {
        return StatCollector.translateToLocal("stat.portal.name");
    }


    // ==================================================
    //                    Background
    // ==================================================
    @Override
    public void drawEnergyBar() {
        // Portal Energy Bar:
        int energyBarWidth = 9;
        int energyBarHeight = 9;
        int energyBarX = this.windowX + 16;
        int energyBarY = this.windowY + 40 - energyBarHeight;
        int energyBarU = 256 - energyBarWidth;
        int energyBarV = 256 - energyBarHeight;

        for(int energyBarEnergyN = 1; energyBarEnergyN <= 10; energyBarEnergyN++) {
            // Empty:
            this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU, energyBarV, energyBarWidth, energyBarHeight);
            // Full:
            if(this.summoningPedestal.capacity >= energyBarEnergyN * this.summoningPedestal.capacityCharge) {
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - (energyBarWidth * 2), energyBarV, energyBarWidth, energyBarHeight);
            }
            // Partial:
            else if(this.summoningPedestal.capacity + this.summoningPedestal.capacityCharge > energyBarEnergyN * this.summoningPedestal.capacityCharge) {
                float spiritChargeScale = (float)(this.summoningPedestal.capacity % this.summoningPedestal.capacityCharge) / (float)this.summoningPedestal.capacityCharge;
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - (energyBarWidth * 2), energyBarV, Math.round((float)energyBarWidth * spiritChargeScale), energyBarHeight);
            }
        }
    }

    @Override
    public void drawHealthBar() {
        // Summoning Progress Bar:
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));

        int barWidth = 80;
        int barHeight = 11;
        int barX = this.centerX + 2;
        int barY = this.windowY + 26;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);

        barWidth = Math.round((float)barWidth * ((float)this.summoningPedestal.summonProgress / this.summoningPedestal.summonProgressMax));
        barV = barV + barHeight;

        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
    }


    // ==================================================
    //                     Controls
    // ==================================================
    @Override
    protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = (this.windowWidth / 4) - (buttonSpacing * 2);
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY;

        this.buttonList.add(new GUITabMain(this.tabButtonID, buttonX, buttonY - 24));

        buttonX = this.centerX + buttonSpacing;
        int buttonXRight = buttonX + buttonWidth + buttonSpacing;
        buttonY = this.windowY + 39 + buttonSpacing;

        // Sitting and Following:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.SITTING.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "..."));

        // Passive and Stance:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.STANCE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

        // PVP:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND_ID.PVP.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "..."));
    }

    @Override
    public void updateButtons(GuiButton button) {
        // Behaviour Buttons:
        if (button.id == EntityCreatureBase.GUI_COMMAND_ID.SITTING.id)
            button.displayString = StatCollector.translateToLocal("gui.pet.sitting") + ": " + (this.summonSet.getSitting() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND_ID.PASSIVE.id)
            button.displayString = StatCollector.translateToLocal("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND_ID.STANCE.id)
            button.displayString = (this.summonSet.getAggressive() ? StatCollector.translateToLocal("gui.pet.aggressive") : StatCollector.translateToLocal("gui.pet.defensive"));

        if (button.id == EntityCreatureBase.GUI_COMMAND_ID.PVP.id)
            button.displayString = StatCollector.translateToLocal("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? StatCollector.translateToLocal("common.yes") : StatCollector.translateToLocal("common.no"));

        // Hidden Mount Buttons:
        if("mount".equals(this.type)) {
            if(button.id >= EntityCreatureBase.GUI_COMMAND_ID.SITTING.id && button.id <= EntityCreatureBase.GUI_COMMAND_ID.PVP.id) {
                button.enabled = false;
                button.visible = false;
            }
        }
    }


    // ==================================================
    //                      Actions
    // ==================================================
    @Override
    public void sendCommandsToServer() {
        this.summoningPedestal.sendSummonSetToServer(this.summonSet);
    }


    // ==================================================
    //                    Pet Selection
    // ==================================================
    @Override
    public void selectMinion(String minionName) {
        this.summonSet.setSummonType(minionName);
        this.sendCommandsToServer();
    }


    // ==================================================
    //                     Has Pets
    // ==================================================
    @Override
    public boolean hasPets() {
        return this.playerExt.getBeastiary().getSummonableList().size() > 0;
    }

    @Override
    public boolean hasSelectedPet() {
        return this.hasPets() && this.summonSet != null && !this.summonSet.summonType.equals("");
    }


    // ==================================================
    //                     Get Texture
    // ==================================================
    @Override
    protected ResourceLocation getTexture() {
        return AssetManager.getTexture("GUIMinionLg");
    }
}
