package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class GUIButtonCreature extends GuiButton {
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
            FontRenderer fontrenderer = minecraft.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            
            int buttonW = this.width;
            int buttonH = this.height;
            int buttonX = this.xPosition;
            int buttonY = this.yPosition;
            minecraft.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
            this.drawImage(buttonX, buttonY, 193, 187 - (hoverState * 32), this.width, this.height, 0.00390625F, 0.00390625F);
            if(mobInfo != null) {
	            Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
	    		this.drawImage(buttonX + 8, buttonY + 8, 0, 0, 16, 16, 0.0625F, 0.0625F);
            }
            
            this.mouseDragged(minecraft, mouseX, mouseY);
            int textColor = 14737632;
            
            if(!this.enabled) {
            	textColor = -6250336;
            }
            else if(this.field_146123_n) {
            	textColor = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, buttonX + 5, buttonY + 2, textColor);
        }
    }
	
	
	// ==================================================
  	//                     Draw Image
  	// ==================================================
	public void drawImage(int x, int y, int u, int v, int w, int h, float s, float t) {
		float z = this.zLevel;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + h), (double)z, (double)((float)(u + 0) * s), (double)((float)(v + h) * t));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h), (double)z, (double)((float)(u + w) * s), (double)((float)(v + h) * t));
        tessellator.addVertexWithUV((double)(x + w), (double)(y + 0), (double)z, (double)((float)(u + w) * s), (double)((float)(v + 0) * t));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)z, (double)((float)(u + 0) * s), (double)((float)(v + 0) * t));
        tessellator.draw();
    }
}
