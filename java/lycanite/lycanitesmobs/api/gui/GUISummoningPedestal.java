package lycanite.lycanitesmobs.api.gui;

import cpw.mods.fml.client.GuiScrollingList;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.network.MessageGUIRequest;
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
    public static void openToPlayer(EntityPlayer player) {
        if(player != null && player.worldObj != null) {
            player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.SUMMONING_PEDESTAL.id, 0, 0);
            MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.SUMMONING_PEDESTAL.id);
            LycanitesMobs.packetHandler.sendToServer(message);
        }
    }

    // ==================================================
    //                    Constructor
    // ==================================================
    public GUISummoningPedestal(EntityPlayer player, TileEntitySummoningPedestal summoningPedestal) {
        super(player, "minion");
        this.summoningPedestal = summoningPedestal;
    }


    // ==================================================
    //                       Init
    // ==================================================
    @Override
    public void initGui() {
        super.initGui();

        // Default Selection: TODO: get active minion from Pedestal.
        if (this.hasPets()) {
            this.selectPet(this.playerExt.petManager.getEntry(this.type, 0));
        }
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
        // TODO: Draw the time until the next minion is summoned.
    }


    // ==================================================
    //                     Get Texture
    // ==================================================
    @Override
    protected ResourceLocation getTexture() {
        return AssetManager.getTexture("GUIMinionLg");
    }
}
