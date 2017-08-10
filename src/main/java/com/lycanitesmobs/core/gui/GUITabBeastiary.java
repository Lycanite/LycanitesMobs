package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GUITabBeastiary extends GUITab {
	
	public GUITabBeastiary(int id) {
        super(id, GUITab.startX, GUITab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    @Override
    public void onTabClicked () {
        GUIBeastiary.openToPlayer(Minecraft.getMinecraft().thePlayer);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
