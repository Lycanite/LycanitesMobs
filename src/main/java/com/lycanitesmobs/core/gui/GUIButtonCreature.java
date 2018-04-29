package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class GUIButtonCreature extends GUIBaseButton {
	public CreatureInfo creatureInfo;

	public GUIButtonCreature(int buttonID, int x, int y, String text, CreatureInfo creatureInfo) {
        super(buttonID, x, y, 32, 32, text);
        this.creatureInfo = creatureInfo;
    }
	
	public GUIButtonCreature(int buttonID, int x, int y, int w, int h, String text, CreatureInfo creatureInfo) {
        super(buttonID, x, y, w, h, text);
        this.creatureInfo = creatureInfo;
    }
	

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.isMouseOver());
            
            int buttonW = this.width;
            int buttonH = this.height;
            int buttonX = this.x;
            int buttonY = this.y;
            mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
            this.drawTexturedModalRect(buttonX, buttonY, 193, 187 - (hoverState * 32), this.width, this.height);
            if(this.creatureInfo != null) {
	            Minecraft.getMinecraft().getTextureManager().bindTexture(creatureInfo.getIcon());
	    		this.drawTexturedModalRect(buttonX + 8, buttonY + 8, 0, 0, 16, 16, 16);
            }
            
            this.mouseDragged(mc, mouseX, mouseY);
            int textColor = 14737632;
            
            if(!this.enabled) {
            	textColor = -6250336;
            }
            else if(this.isMouseOver()) {
            	textColor = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, buttonX + 5, buttonY + 2, textColor);
        }
    }
}
