package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class GUIButtonCreature extends GUIBaseButton {
	public MobInfo mobInfo;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIButtonCreature(int buttonID, int x, int y, String text, MobInfo mobInfo) {
        super(buttonID, x, y, 32, 32, text);
        this.mobInfo = mobInfo;
    }
	
	public GUIButtonCreature(int buttonID, int x, int y, int w, int h, String text, MobInfo mobInfo) {
        super(buttonID, x, y, w, h, text);
        this.mobInfo = mobInfo;
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
            this.drawTexturedModalRect(buttonX, buttonY, 193, 187 - (hoverState * 32), this.width, this.height);
            if(mobInfo != null) {
	            Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
	    		this.drawTexturedModalRect(buttonX + 8, buttonY + 8, 0, 0, 16, 16, 16);
            }
            
            this.mouseDragged(minecraft, mouseX, mouseY);
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
