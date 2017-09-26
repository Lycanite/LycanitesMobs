package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GUIButtonTab extends GUIBaseButton {
	ResourceLocation texture;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIButtonTab(int buttonID, int x, int y, ResourceLocation texture) {
        super(buttonID, x, y, 32, 32, "");
        this.texture = texture;
    }
	
	public GUIButtonTab(int buttonID, int x, int y, int w, int h, ResourceLocation texture) {
        super(buttonID, x, y, w, h, "");
        this.texture = texture;
    }
	
	
	// ==================================================
  	//                   Draw Button
  	// ==================================================
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
            this.drawTexturedModalRect(buttonX, buttonY, 32, hoverState * 32, this.width, this.height);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
    		this.drawTexturedModalRect(buttonX + 4, buttonY + 4, 0, 0, 16, 16, 16);
            
            this.mouseDragged(mc, mouseX, mouseY);
            int textColor = 14737632;
            
            if(!this.enabled) {
            	textColor = -6250336;
            }
            else if(this.isMouseOver()) {
            	textColor = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, buttonX + buttonW / 2, buttonY + (buttonH - 8) / 2, textColor);
        }
    }
}
