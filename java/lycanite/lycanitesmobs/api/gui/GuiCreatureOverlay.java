package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

public class GuiCreatureOverlay extends Gui {
	public Minecraft mc;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public GuiCreatureOverlay(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	
    // ==================================================
    //                     Draw GUI
    // ==================================================
	@ForgeSubscribe
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if(event.isCanceled() || event.type != ElementType.EXPERIENCE)
	      return;
		
		ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		int sWidth = scaledresolution.getScaledWidth();
        int sHeight = scaledresolution.getScaledHeight();
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.mc.thePlayer);
		if(playerExt != null && !this.mc.thePlayer.capabilities.isCreativeMode && (
				playerExt.summonFocus < playerExt.summonFocusMax ||
				this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemStaffSummoning
			)) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
			
			int barYSpace = 10;
			int barXSpace = -1;
			
            int summonBarWidth = 9;
            int summonBarHeight = 9;
            int summonBarX = (sWidth / 2) + 10;
            int summonBarY = sHeight - 30 - summonBarHeight;
            int summonBarU = 256 - summonBarWidth;
            int summonBarV = 256 - summonBarHeight;
            
            summonBarY -= barYSpace;
            if(this.mc.thePlayer.isInsideOfMaterial(Material.water))
            	summonBarY -= barYSpace;
            
            for(int summonBarEnergyN = 0; summonBarEnergyN < 10; summonBarEnergyN++) {
            	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU, summonBarV, summonBarWidth, summonBarHeight);
            	if(playerExt.summonFocus >= playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
                	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU - summonBarWidth, summonBarV, summonBarWidth, summonBarHeight);
            	}
                else if(playerExt.summonFocus + playerExt.summonFocusCharge > playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
            		float summonChargeScale = (float)(playerExt.summonFocus % playerExt.summonFocusCharge) / (float)playerExt.summonFocusCharge;
            		this.drawTexturedModalRect((summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN)) + (summonBarWidth - Math.round((float)summonBarWidth * summonChargeScale)), summonBarY, summonBarU - Math.round((float)summonBarWidth * summonChargeScale), summonBarV, Math.round((float)summonBarWidth * summonChargeScale), summonBarHeight);
            	}
            }
		}
		
		// ========== Mount Stamina Bar ==========
		if(this.mc.thePlayer.ridingEntity != null && this.mc.thePlayer.ridingEntity instanceof EntityCreatureRideable) {
			EntityCreatureRideable mount = (EntityCreatureRideable)this.mc.thePlayer.ridingEntity;
            float mountStamina = mount.getStaminaPercent();
            
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(Gui.icons);
            int staminaBarWidth = 182;
            int staminaBarHeight = 5;
            int staminaEnergyWidth = (int)((float)(staminaBarWidth + 1) * mountStamina);
            int staminaBarX = (sWidth / 2) - (staminaBarWidth / 2);
            int staminaBarY = sHeight - 32 + 3;
            int staminaTextureY = 84;
            if(mount.getStaminaType() == "toggle")
            	staminaTextureY -= staminaBarHeight * 2;
            int staminaEnergyY = staminaTextureY + staminaBarHeight;
            
            this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaTextureY, staminaBarWidth, staminaBarHeight);
            if(staminaEnergyWidth > 0)
                this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaEnergyY, staminaEnergyWidth, staminaBarHeight);
		}
		
		this.mc.getTextureManager().bindTexture(Gui.icons);
	}
}
