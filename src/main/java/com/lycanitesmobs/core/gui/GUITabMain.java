package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GUITabMain extends GUITab {

	public GUITabMain(int id) {
        super(id, GUITab.startX, GUITab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    public GUITabMain(int id, int x, int y) {
        super(id, x, y, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    @Override
    public void onTabClicked () {
        GUILMMainMenu.openToPlayer(Minecraft.getMinecraft().player);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
