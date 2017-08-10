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
	// field_82253_i = mouseOver
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if(this.visible) {
            FontRenderer fontrenderer = minecraft.fontRendererObj;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.isMouseOver());
            
            int buttonW = this.width;
            int buttonH = this.height;
            int buttonX = this.xPosition;
            int buttonY = this.yPosition;
            minecraft.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
            this.drawTexturedModalRect(buttonX, buttonY, 32, hoverState * 32, this.width, this.height);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
    		this.drawTexturedModalRect(buttonX + 4, buttonY + 4, 0, 0, 16, 16, 16);
            
            this.mouseDragged(minecraft, mouseX, mouseY);
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
