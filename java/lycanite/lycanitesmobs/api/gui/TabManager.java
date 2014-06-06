package lycanite.lycanitesmobs.api.gui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import cpw.mods.fml.client.FMLClientHandler;

public class TabManager {
	public static ArrayList<GUITab> tabList = new ArrayList<GUITab>();

    public static void registerTab (GUITab tab) {
        tabList.add(tab);
    }

    public static ArrayList<GUITab> getTabList () {
        return tabList;
    }
    
    public static void addTabsToInventory (GuiScreen gui) {
        if(gui.getClass() == GuiInventory.class) {
        	
        	GuiInventorySnooper guiInventorySnooper = new GuiInventorySnooper(mc.thePlayer);
        	
        	try {
            	Field field = GuiScreen.class.getDeclaredField(guiInventorySnooper.getButtonListFieldName());
	            field.setAccessible(true);
	            List buttonList = (List)field.get(gui);
	            buttonList.clear();
	            addTabsToList(buttonList);
	            field.set(gui, buttonList);
			} catch(Exception e) {
				LycanitesMobs.printWarning("", "A problem occured when adding custom inventory tabs:");
				e.printStackTrace();
			}
        }
    }

    private static Minecraft mc = FMLClientHandler.instance().getClient();

    public static void openInventoryGui () {
        GuiInventory inventory = new GuiInventory(mc.thePlayer);
        mc.displayGuiScreen(inventory);
        TabManager.addTabsToInventory(inventory);
    }

    public static void updateTabValues (int cornerX, int cornerY, Class<?> selectedButton) {
        int count = 2;
        for (int i = 0; i < tabList.size(); i++) {
        	GUITab t = tabList.get(i);

            if (t.shouldAddToList()) {
                t.id = count;
                t.xPosition = cornerX + (count - 2) * 28;
                t.yPosition = cornerY - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                count++;
            }
        }
    }

    public static void addTabsToList (List buttonList) {
        for(GUITab tab : tabList) {
            if(tab.shouldAddToList()) {
                buttonList.add(tab);
            }
        }
    }
}
