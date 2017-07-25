package lycanite.lycanitesmobs.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class GUITab extends GUIBaseButton {
	public ResourceLocation texture = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    public ResourceLocation icon = null;
    public static int startX = 0;
    public static int startY = 0;
    public static int tabWidth = 28;
    public static int tabHeight = 32;
    
    public int tabID = 0;

    public GUITab(int id, int posX, int posY, ResourceLocation icon) {
        super(550 + id, posX, posY, tabWidth, tabHeight, "");
        this.icon = icon;
        this.tabID = id;
    }

    @Override
    public void drawButton (Minecraft mc, int mouseX, int mouseY)  {
        if(this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int yTexPos = this.enabled ? 0 : tabHeight;
            int ySize = this.enabled ? tabWidth : tabHeight;
            int xOffset = this.id == 2 ? 0 : 1;
            
            int tabX = this.xPosition;
            int tabY = this.yPosition;
            
            if(mc.currentScreen != null && mc.currentScreen instanceof GuiInventory) {
            	GuiInventory guiInventory = (GuiInventory)mc.currentScreen;
            	GuiInventorySnooper guiInventorySnooper = new GuiInventorySnooper(mc.player);
            	tabX += (guiInventory.width / 2) + (guiInventorySnooper.getGUIXSize() / 2);
    			tabX -= tabWidth * (this.tabID + 1);
    			tabX -= 4;
    			
    			tabY += (guiInventory.height / 2) - (guiInventorySnooper.getGUIYSize() / 2);
    			tabY -= ySize; 
            }

            mc.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect(tabX, tabY, xOffset * 28, yTexPos, 28, ySize);
            if(this.icon != null) {
            	mc.renderEngine.bindTexture(this.icon);
            	this.drawTexturedModalRect(tabX + 6, tabY + 10, 0, 0, 16, 16, 16);
            }
        }
    }

    @Override
    public boolean mousePressed (Minecraft mc, int mouseX, int mouseY) {
        int ySize = this.enabled ? tabWidth : tabHeight;
        int tabX = this.xPosition;
        int tabY = this.yPosition;
        	
    	if(mc.currentScreen != null && mc.currentScreen instanceof GuiInventory) {
        	GuiInventory guiInventory = (GuiInventory)mc.currentScreen;
        	GuiInventorySnooper guiInventorySnooper = new GuiInventorySnooper(mc.player);
        	tabX += (guiInventory.width / 2) + (guiInventorySnooper.getGUIXSize() / 2);
			tabX -= tabWidth * (this.tabID + 1);
			tabX -= 4;
			
			tabY += (guiInventory.height / 2) - (guiInventorySnooper.getGUIYSize() / 2);
			tabY -= ySize; 
        }
        
        boolean inWindow = this.enabled && this.visible && mouseX >= tabX && mouseY >= tabY && mouseX < tabX + this.width && mouseY < tabY + this.height;
        if(inWindow)
            this.onTabClicked();
        return inWindow;
    }

    public abstract void onTabClicked();

    public abstract boolean shouldAddToList();
}
