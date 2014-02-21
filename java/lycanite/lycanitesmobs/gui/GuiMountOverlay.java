package lycanite.lycanitesmobs.gui;

import lycanite.lycanitesmobs.entity.EntityCreatureRideable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

public class GuiMountOverlay extends Gui {
	public Minecraft mc;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public GuiMountOverlay(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	
    // ==================================================
    //                     Draw GUI
    // ==================================================
	@ForgeSubscribe
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if(event.isCanceled() || event.type != ElementType.EXPERIENCE)
	      return;
		
		// ========== Stamina Bar ==========
		if(this.mc.thePlayer.ridingEntity != null && this.mc.thePlayer.ridingEntity instanceof EntityCreatureRideable) {
			ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
			int sWidth = scaledresolution.getScaledWidth();
	        int sHeight = scaledresolution.getScaledHeight();
			
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
	}
}
