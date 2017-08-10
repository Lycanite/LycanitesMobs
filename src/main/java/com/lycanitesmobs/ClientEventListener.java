package com.lycanitesmobs;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventListener {

    // ==================================================
    //                Client Fog Color
    // ==================================================
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if(!(event.getEntity() instanceof EntityLivingBase))
            return;
        EntityLivingBase entityLiving = (EntityLivingBase)event.getEntity();
        if(event.getState().getMaterial() == Material.LAVA && (!event.getEntity().isBurning() || entityLiving.isPotionActive(MobEffects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }
    }


    // ==================================================
    //                First Person Fire
    // ==================================================
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isBurning() || event.getPlayer().isPotionActive(MobEffects.FIRE_RESISTANCE))) {
            event.setCanceled(true);
        }
    }
}
