package lycanite.lycanitesmobs.api.gui;

import cpw.mods.fml.common.eventhandler.EventPriority;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.KeyHandler;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiOverlay extends Gui {
	public Minecraft mc;
	
	private int mountMessageTimeMax = 10 * 20;
	private int mountMessageTime = 0;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public GuiOverlay(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	
    // ==================================================
    //                  Draw Game Overlay
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if(event.isCancelable() || event.type != ElementType.EXPERIENCE)
	      return;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
		
		ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		int sWidth = scaledresolution.getScaledWidth();
        int sHeight = scaledresolution.getScaledHeight();

        // ========== Mob Events Title ==========
        if(MobEventManager.instance.clientMobEvent != null)
            MobEventManager.instance.clientMobEvent.onGUIUpdate(this, sWidth, sHeight);
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.mc.thePlayer);
		if(playerExt != null && !this.mc.thePlayer.capabilities.isCreativeMode
                && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemStaffSummoning) {
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
            
            // Mount Controls Message:
            if(this.mountMessageTime > 0) {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	if(this.mountMessageTime < 60)
            		GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)this.mountMessageTime / (float)60);
            	String mountMessage = StatCollector.translateToLocal("gui.mount.controls");
            	mountMessage = mountMessage.replace("%control%", GameSettings.getKeyDisplayString(KeyHandler.instance.mountAbility.getKeyCode()));
            	int stringWidth = this.mc.fontRenderer.getStringWidth(mountMessage);
            	this.mc.fontRenderer.drawString(mountMessage, (sWidth / 2) - (stringWidth / 2), sHeight - 64, 0xFFFFFF);
            }
            
            // Mount Ability Stamina Bar:
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(Gui.icons);
            int staminaBarWidth = 182;
            int staminaBarHeight = 5;
            int staminaEnergyWidth = (int)((float)(staminaBarWidth + 1) * mountStamina);
            int staminaBarX = (sWidth / 2) - (staminaBarWidth / 2);
            int staminaBarY = sHeight - 32 + 3;
            int staminaTextureY = 84;
            if("toggle".equals(mount.getStaminaType()))
            	staminaTextureY -= staminaBarHeight * 2;
            int staminaEnergyY = staminaTextureY + staminaBarHeight;
            
            this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaTextureY, staminaBarWidth, staminaBarHeight);
            if(staminaEnergyWidth > 0)
                this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaEnergyY, staminaEnergyWidth, staminaBarHeight);
            
            if(this.mountMessageTime > 0)
            	this.mountMessageTime--;
		}
		else
			this.mountMessageTime = this.mountMessageTimeMax;

        GL11.glPopMatrix();
		this.mc.getTextureManager().bindTexture(Gui.icons);
	}
}
