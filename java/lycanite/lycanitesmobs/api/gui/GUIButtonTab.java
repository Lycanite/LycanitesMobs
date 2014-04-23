package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUIButtonTab extends GuiButton {
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
        if(this.drawButton) {
            FontRenderer fontrenderer = minecraft.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_82253_i);
            
            int buttonW = this.width;
            int buttonH = this.height;
            int buttonX = this.xPosition;
            int buttonY = this.yPosition;
            minecraft.getTextureManager().bindTexture(AssetManager.getTexture("GUIControls"));
            this.drawImage(buttonX, buttonY, 32, hoverState * 32, this.width, this.height, 0.125F, 0.125F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
    		this.drawImage(buttonX + 4, buttonY + 4, 0, 0, 16, 16, 0.0625F, 0.0625F);
            
            this.mouseDragged(minecraft, mouseX, mouseY);
            int textColor = 14737632;
            
            if(!this.enabled) {
            	textColor = -6250336;
            }
            else if(this.field_82253_i) {
            	textColor = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, buttonX + buttonW / 2, buttonY + (buttonH - 8) / 2, textColor);
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
