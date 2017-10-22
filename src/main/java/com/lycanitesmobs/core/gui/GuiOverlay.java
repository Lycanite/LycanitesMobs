package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.*;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.item.ItemStaffSummoning;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class GuiOverlay extends GUIBase {
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
        if(LycanitesMobs.proxy.getClientPlayer() == null)
            return;
        EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();

		if(event.isCancelable() || event.getType() != ElementType.EXPERIENCE)
	      return;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int sWidth = scaledresolution.getScaledWidth();
        int sHeight = scaledresolution.getScaledHeight();

        // ========== Mob/World Events Title ==========
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(player.getEntityWorld());
        if(worldExt != null) {
            for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values())
                mobEventPlayerClient.onGUIUpdate(this, sWidth, sHeight);
            if(worldExt.clientWorldEventPlayer != null)
                worldExt.clientWorldEventPlayer.onGUIUpdate(this, sWidth, sHeight);
        }
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null && !this.mc.player.capabilities.isCreativeMode && (
                (this.mc.player.getHeldItem(EnumHand.MAIN_HAND) != null && this.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemStaffSummoning)
                || (this.mc.player.getHeldItem(EnumHand.OFF_HAND) != null && this.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemStaffSummoning)
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
            if(this.mc.player.isInsideOfMaterial(Material.WATER))
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
		if(this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() instanceof EntityCreatureRideable) {
			EntityCreatureRideable mount = (EntityCreatureRideable)this.mc.player.getRidingEntity();
            float mountStamina = mount.getStaminaPercent();
            
            // Mount Controls Message:
            if(this.mountMessageTime > 0) {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	if(this.mountMessageTime < 60)
            		GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)this.mountMessageTime / (float)60);
            	String mountMessage = I18n.translateToLocal("gui.mount.controls");
            	mountMessage = mountMessage.replace("%control%", GameSettings.getKeyDisplayString(KeyHandler.instance.mountAbility.getKeyCode()));
            	int stringWidth = this.mc.fontRenderer.getStringWidth(mountMessage);
            	this.mc.fontRenderer.drawString(mountMessage, (sWidth / 2) - (stringWidth / 2), sHeight - 64, 0xFFFFFF);
            }
            
            // Mount Ability Stamina Bar:
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(ICONS);
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
		this.mc.getTextureManager().bindTexture(ICONS);
	}
}
