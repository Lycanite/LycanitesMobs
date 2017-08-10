package com.lycanitesmobs.core.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GUIBaseButton extends GuiButton {

    // ==================================================
    //                    Constructor
    // ==================================================
    public GUIBaseButton(int buttonID, int x, int y, int width, int height, String text) {
        super(buttonID, x, y, width, height, text);
    }


    // ==================================================
    //                   Draw Texture
    // ==================================================
    @Override
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
        float scaleX = 0.00390625F * resolution;
        float scaleY = scaleX;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }
}
