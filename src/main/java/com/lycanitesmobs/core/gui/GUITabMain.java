package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiaryIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiTabMain extends GuiTab {

	public GuiTabMain(int id) {
        super(id, GuiTab.startX, GuiTab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    public GuiTabMain(int id, int x, int y) {
        super(id, x, y, new ResourceLocation(LycanitesMobs.modid, "textures/items/soulgazer.png"));
    }

    @Override
    public void onTabClicked () {
        GuiBeastiaryIndex.openToPlayer(Minecraft.getMinecraft().player);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
